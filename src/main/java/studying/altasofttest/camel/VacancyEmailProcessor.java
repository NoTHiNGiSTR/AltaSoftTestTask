package studying.altasofttest.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import studying.altasofttest.models.Subscriber;
import studying.altasofttest.models.Vacancy;
import java.time.LocalDate;
import java.util.List;


@Component
public class VacancyEmailProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        @SuppressWarnings("unchecked")
        List<Vacancy> vacancies = exchange.getIn().getBody(List.class);
        Subscriber subscriber = (Subscriber) exchange.getIn().getHeader("applicant");

        StringBuilder body = new StringBuilder();
        body
                .append("Здравствуйте, ")
                .append(subscriber.getFullName())
                .append("!\n\n")
                .append("Информируем вас о новых вакансиях на должность:")
                .append(subscriber.getDesiredPosition())
                .append("\n\n");

        for (Vacancy vacancy : vacancies) {
            body
                    .append("Наименование: ").append(vacancy.getName()).append("\n")
                    .append("Описание: ").append(vacancy.getDescription()).append("\n")
                    .append("Уровень зарплаты: ").append(vacancy.getSalary() != null ? vacancy.getSalary() : "не указано").append("\n")
                    .append("Требуемый опыт: ").append(vacancy.getExperience() != null ? vacancy.getExperience() : "не указано").append("\n")
                    .append("---------------------------\n");
        }

        body
                .append("\nС уважением,\nЦифровое Будущее\n")
                .append(LocalDate.now());

        exchange.getIn().setBody(body.toString());
        exchange.getIn().setHeader("To", subscriber.getEmail());
        exchange.getIn().setHeader("Subject", "Новые вакансии для вас");
        exchange.getIn().setHeader("Content-Type", "text/plain; charset=UTF-8");
    }
}

