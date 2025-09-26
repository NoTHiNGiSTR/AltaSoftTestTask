package studying.altasofttest;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import studying.altasofttest.camel.VacancyEmailProcessor;
import studying.altasofttest.models.Subscriber;
import studying.altasofttest.models.Vacancy;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VacancyEmailProcessorTest {

    private VacancyEmailProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new VacancyEmailProcessor();
    }

    @Test
    void testProcess() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Subscriber subscriber = new Subscriber();
        subscriber.setFullName("Иван Иванов");
        subscriber.setEmail("ivan@example.com");
        subscriber.setDesiredPosition("Java Developer");

        Vacancy vacancy = new Vacancy();
        vacancy.setName("Java Dev");
        vacancy.setDescription("Backend dev");
        vacancy.setSalary(BigDecimal.valueOf(150000));
        vacancy.setExperience("3+ years");

        exchange.getIn().setBody(List.of(vacancy));
        exchange.getIn().setHeader("applicant", subscriber);

        processor.process(exchange);

        String body = exchange.getIn().getBody(String.class);
        assertTrue(body.contains("Иван Иванов"));
        assertTrue(body.contains("Java Dev"));
        assertEquals("ivan@example.com", exchange.getIn().getHeader("To"));
        assertEquals("Новые вакансии для вас", exchange.getIn().getHeader("Subject"));
    }
}

