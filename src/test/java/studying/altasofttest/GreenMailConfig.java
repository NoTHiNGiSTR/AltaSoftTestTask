package studying.altasofttest;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import studying.altasofttest.camel.VacancyEmailProcessor;

@TestConfiguration
public class GreenMailConfig {

    @Bean(destroyMethod = "stop")
    public GreenMail greenMail() {
        GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
        return greenMail;
    }

    @Bean
    public RouteBuilder testRoute(VacancyEmailProcessor emailProcessor) {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:sendTest")
                        .process(emailProcessor)
                        .to("smtp://localhost:3025"); // GreenMail SMTP
            }
        };
    }
}

