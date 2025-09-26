package studying.altasofttest.services.implementations;

import org.apache.camel.ProducerTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studying.altasofttest.dto.VacancyForSubscriber;
import studying.altasofttest.models.Subscriber;
import studying.altasofttest.models.Vacancy;
import studying.altasofttest.repositories.SubscriberRepository;
import studying.altasofttest.repositories.VacancyRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class VacancyNotificationService {

    private final SubscriberRepository subscriberRepository;
    private final VacancyRepository vacancyRepository;

    private static final int PAGE_SIZE = 1000;
    private static final int MAX_VACANCIES_PER_SUBSCRIBER = 5;
    private static final int THREAD_POOL_SIZE = 10;

    public VacancyNotificationService(SubscriberRepository subscriberRepository, VacancyRepository vacancyRepository) {
        this.subscriberRepository = subscriberRepository;
        this.vacancyRepository = vacancyRepository;
    }



    @Transactional(readOnly = true)
    public void streamVacancyNotifications(ProducerTemplate template)
            throws InterruptedException, ExecutionException {
        Timestamp twoMinutesAgo = Timestamp.valueOf(LocalDateTime.now().minusMinutes(2));
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        int page = 0;
        Page<Subscriber> subscribersPage;

        do { //Достаём подписчиков из бд партиями, чтобы избежать переполнения памяти
            subscribersPage = subscriberRepository.findAll(PageRequest.of(page, PAGE_SIZE));
            List<Subscriber> subscribers = subscribersPage.getContent();
            if (!subscribers.isEmpty()) {
                List<VacancyForSubscriber> pageResults = processSubscribersPage(subscribers, twoMinutesAgo, executor);
                for (VacancyForSubscriber vfs : pageResults) { //Для каждой партии отправляем в camel список совпадений подписчик-вакансии
                    template.sendBody("direct:sendVacancyEmail", vfs);
                }
            }
            page++;
        } while (!subscribersPage.isLast());

        executor.shutdown();
    }



    private List<VacancyForSubscriber> processSubscribersPage(List<Subscriber> subscribers,
                                                              Timestamp since,
                                                              ExecutorService executor)
            throws InterruptedException, ExecutionException {
        List<String> positions = extractPositions(subscribers);
        List<String> cities = extractCities(subscribers);

        List<Vacancy> newVacancies = vacancyRepository.findNewVacanciesForSubscribers(
                positions, cities, since
        );

        Map<String, Map<String, List<Vacancy>>> vacancyMap = groupVacancies(newVacancies);

        return getMatchList(subscribers, vacancyMap, executor);
    }

    private List<String> extractPositions(List<Subscriber> subscribers) { //Получаем уникальный список позиций
        return subscribers.stream()
                .map(Subscriber::getDesiredPosition)
                .distinct()
                .toList();
    }


    private List<String> extractCities(List<Subscriber> subscribers) { //Получаем уникальный список городов
        return subscribers.stream()
                .map(Subscriber::getCity)
                .filter(c -> !"не важно".equalsIgnoreCase(c))
                .distinct()
                .toList();
    }

    private Map<String, Map<String, List<Vacancy>>> groupVacancies(List<Vacancy> vacancies) { //Группируем вакансии в Map<Позиция, Map<Городб List<Вакансия>>
        return vacancies.stream().collect(
                Collectors.groupingBy(
                        Vacancy::getPosition,
                        Collectors.groupingBy(
                                Vacancy::getCity
                        )
                )
        );
    }



    private List<VacancyForSubscriber> getMatchList(List<Subscriber> subscribers,  //Собираем список совпадений Подписчик-вакансии
                                                    Map<String,
                                                    Map<String,
                                                    List<Vacancy>>> vacancies,
                                                    ExecutorService executor)
            throws InterruptedException, ExecutionException {
        List<Future<VacancyForSubscriber>> futures = new ArrayList<>();

        for (Subscriber subscriber : subscribers) {
            futures.add(
                    executor.submit(() -> matchVacanciesForSubscriber(subscriber, vacancies))
            );
        }
        List<VacancyForSubscriber> result = new ArrayList<>();
        for (Future<VacancyForSubscriber> f : futures) {
            VacancyForSubscriber vfs = f.get();
            if (vfs != null) result.add(vfs);
        }
        return result;
    }

    private VacancyForSubscriber matchVacanciesForSubscriber(Subscriber subscriber, Map<String, Map<String, List<Vacancy>>> vacancyMap) {
        Map<String, List<Vacancy>> positionMap = vacancyMap.getOrDefault(subscriber.getDesiredPosition(), Map.of());
        List<Vacancy> matched;
        if ("не важно".equalsIgnoreCase(subscriber.getCity())) { //Если город не указан то берём любые вакансии
            matched = positionMap.values().stream()
                    .flatMap(
                            List::stream
                    )
                    .limit(MAX_VACANCIES_PER_SUBSCRIBER)
                    .toList();
        } else {
            matched = positionMap //берём вакансии подходящие по городу
                    .getOrDefault(subscriber.getCity(), List.of())
                    .stream()
                    .limit(MAX_VACANCIES_PER_SUBSCRIBER)
                    .toList();
        }

        if (!matched.isEmpty()){
            return new VacancyForSubscriber(subscriber, matched);
        }
        return null;
    }

}



