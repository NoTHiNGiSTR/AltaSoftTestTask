package studying.altasofttest.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import studying.altasofttest.dto.VacancyForSubscriber;
import studying.altasofttest.services.implementations.VacancyNotificationService;

@Component
public class VacancyNotificationRoute extends RouteBuilder {

    private final VacancyNotificationService notificationService;
    private final VacancyEmailProcessor emailProcessor;

    public VacancyNotificationRoute(VacancyNotificationService notificationService,
                                    VacancyEmailProcessor emailProcessor) {
        this.notificationService = notificationService;
        this.emailProcessor = emailProcessor;
    }

    @Override
    public void configure() {
        from("quartz://vacancyNotification?cron=0+0/2+*+*+*+?")
                .routeId("vacancyNotificationRoute")
                .process(exchange ->
                        notificationService.streamVacancyNotifications(exchange.getContext().createProducerTemplate()));

        // Параллельная обработка каждой VacancyForSubscriber
        from("direct:sendVacancyEmail")
                .split(body()).parallelProcessing()
                .process(exchange -> {
                    VacancyForSubscriber vfs = exchange.getIn().getBody(VacancyForSubscriber.class);
                    exchange.getIn().setHeader("vacancies", vfs.vacancies());
                    exchange.getIn().setHeader("applicant", vfs.subscriber());
                    exchange.getIn().setBody(vfs.vacancies());
                })
                .process(emailProcessor)
                .to("smtp://{{spring.mail.host}}:{{spring.mail.port}}"
                        + "?username={{spring.mail.username}}"
                        + "&password={{spring.mail.password}}"
                        + "&mail.smtp.auth={{spring.mail.properties.mail.smtp.auth}}"
                        + "&mail.smtp.starttls.enable={{spring.mail.properties.mail.smtp.starttls.enable}}");
    }
}



