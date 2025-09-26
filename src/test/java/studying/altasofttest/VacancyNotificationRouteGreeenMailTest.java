package studying.altasofttest;
import com.icegreen.greenmail.util.GreenMail;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import studying.altasofttest.dto.VacancyForSubscriber;
import studying.altasofttest.models.Subscriber;
import studying.altasofttest.models.Vacancy;
import studying.altasofttest.services.implementations.VacancyNotificationService;

import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Import(GreenMailConfig.class)
class VacancyNotificationRouteGreenMailTest {

    @MockBean
    private VacancyNotificationService vacancyNotificationService;

    @Autowired
    private GreenMail greenMail;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    void testVacancyEmailSending() throws Exception {
        // Подготовка данных
        Subscriber subscriber = new Subscriber();
        subscriber.setFullName("Иван Иванов");
        subscriber.setEmail("ivan@example.com");
        subscriber.setDesiredPosition("Java Developer");

        Vacancy vacancy = new Vacancy();
        vacancy.setName("Java Dev");
        vacancy.setDescription("Разработка бэкенда");
        vacancy.setSalary(BigDecimal.valueOf(150000));
        vacancy.setExperience("3 года");

        VacancyForSubscriber vfs = new VacancyForSubscriber(subscriber, List.of(vacancy));

        // Отправляем Exchange в маршрут
        producerTemplate.send("direct:sendTest", exchange -> {
            // тело письма — список вакансий
            exchange.getIn().setBody(vfs.vacancies());
            // subscriber кладём в header
            exchange.getIn().setHeader("applicant", vfs.subscriber());
        });

        // Проверяем, что письмо пришло
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);

        MimeMessage message = messages[0];
        String body = (String) message.getContent();

        assertTrue(body.contains("Иван Иванов"));
        System.out.println("/////////////////////////////////\n\n" + message.getHeader("applicant")+"\n\n" + message.getSubject() + "\n" + message.getContent() + "\n/////////////////////////////////\n\n\n\n");
        assertTrue(body.contains("Java Dev"));
        assertEquals("Новые вакансии для вас", message.getSubject());
        assertEquals("ivan@example.com", message.getAllRecipients()[0].toString());
    }
}





