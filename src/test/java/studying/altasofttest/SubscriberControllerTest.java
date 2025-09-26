package studying.altasofttest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import studying.altasofttest.controllers.SubscriberController;
import studying.altasofttest.dto.SubscriberDto;
import studying.altasofttest.exceptions.GlobalExceptionHandler;
import studying.altasofttest.services.SubscriberService;


@WebMvcTest(controllers = SubscriberController.class)
@Import(GlobalExceptionHandler.class)
class SubscriberControllerTest {

    @MockBean
    SubscriberService subscriberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void subscriptionTest() throws Exception {
        SubscriberDto testSubscriber = SubscriberDto.builder()
                .email("example@mail.ru")
                .fullName("TestName")
                .desiredPosition("TestPosition")
                .city("TestCity")
                .build();

        SubscriberDto savedSubscriber = SubscriberDto.builder()
                .id(1L)
                .email("example@mail.ru")
                .fullName("TestName")
                .desiredPosition("TestPosition")
                .city("TestCity")
                .build();
        Mockito.when(subscriberService.createSubscriber(Mockito.any(SubscriberDto.class)))
                .thenReturn(savedSubscriber);
        String jsonRequest = objectMapper.writeValueAsString(testSubscriber);
        mockMvc.perform(MockMvcRequestBuilders.put("/subscriber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("example@mail.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("TestName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("TestCity"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.desiredPosition").value("TestPosition"));
    }

    @Test
    void subscriptionWithNonSelectedCityTest() throws Exception {
        SubscriberDto testSubscriber = SubscriberDto.builder()
                .email("example@mail.ru")
                .fullName("TestName")
                .desiredPosition("TestPosition")
                .city(null)
                .build();

        SubscriberDto savedSubscriber = SubscriberDto.builder()
                .id(1L)
                .email("example@mail.ru")
                .fullName("TestName")
                .desiredPosition("TestPosition")
                .city("Не важно")
                .build();
        Mockito.when(subscriberService.createSubscriber(Mockito.any(SubscriberDto.class)))
                .thenReturn(savedSubscriber);
        String jsonRequest = objectMapper.writeValueAsString(testSubscriber);
        mockMvc.perform(MockMvcRequestBuilders.put("/subscriber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("example@mail.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("TestName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Не важно"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.desiredPosition").value("TestPosition"));
    }

    @Test
    void subscriptionWithInvalidArgumentTest() throws Exception {
        SubscriberDto testSubscriber = SubscriberDto.builder()
                .email("")
                .fullName("")
                .desiredPosition("")
                .city("")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(testSubscriber);
        mockMvc.perform(MockMvcRequestBuilders.put("/subscriber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.fullName").value("Name must not be empty"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.desiredPosition").value("Desired position must not be empty"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.email").value("Email must not be empty"));

    }

    @Test
    void subscriptionWithInvalidEmailFormat() throws Exception {
        SubscriberDto testSubscriber = SubscriberDto.builder()
                .email("invalidFormat")
                .fullName("TestName")
                .desiredPosition("TestPosition")
                .city("TestCity")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(testSubscriber);
        mockMvc.perform(MockMvcRequestBuilders.put("/subscriber")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.email").value("Invalid email format"));
    }
}
