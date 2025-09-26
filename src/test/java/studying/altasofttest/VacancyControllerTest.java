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
import studying.altasofttest.controllers.VacancyController;
import studying.altasofttest.dto.VacancyDto;
import studying.altasofttest.exceptions.GlobalExceptionHandler;
import studying.altasofttest.models.Vacancy;
import studying.altasofttest.services.VacancyService;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest(controllers = VacancyController.class)
@Import(GlobalExceptionHandler.class)
class VacancyControllerTest {


    @MockBean
    VacancyService vacancyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void vacancyCreationTest() throws Exception {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("TestVacancyName")
                .description("TestVacancyDescription")
                .position("TestPosition")
                .salary(BigDecimal.valueOf(80000))
                .experience("TestExperience")
                .city("TestCity")
                .build();

        VacancyDto savedVacancy = VacancyDto.builder()
                .id(1L)
                .name("TestVacancyName")
                .description("TestVacancyDescription")
                .position("TestPosition")
                .salary(BigDecimal.valueOf(80000))
                .experience("TestExperience")
                .city("TestCity")
                .build();
        Mockito.when(vacancyService.createVacancy(Mockito.any(VacancyDto.class)))
                .thenReturn(savedVacancy);
        String jsonRequest = objectMapper.writeValueAsString(vacancyDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestVacancyName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("TestVacancyDescription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(BigDecimal.valueOf(80000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.position").value("TestPosition"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.experience").value("TestExperience"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("TestCity"));
    }

    @Test
    void vacancyCreationWithInvalidDataTest() throws Exception {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("")
                .description("")
                .position("")
                .salary(BigDecimal.valueOf(-80000))
                .experience("")
                .city("")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(vacancyDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/vacancy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.name").value("Vacancy name must not be empty"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.salary").value("Salary must be positive"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.position").value("Position must not be empty"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.city").value("City must not be empty"));
    }


    @Test
    void shouldReturnAllVacancies() throws Exception {
        List<Vacancy> vacancies = List.of(
                Vacancy.builder()
                        .id(1L)
                        .name("Java Developer")
                        .description("Backend dev")
                        .position("Backend")
                        .salary(BigDecimal.valueOf(150000))
                        .experience("3 years")
                        .city("Moscow")
                        .build(),
                Vacancy.builder()
                        .id(2L)
                        .name("QA Engineer")
                        .description("Testing APIs")
                        .position("Testing")
                        .salary(BigDecimal.valueOf(100000))
                        .experience("1 year")
                        .city("SPb")
                        .build()
        );

        Mockito.when(vacancyService.getVacancies(null, null, null)).thenReturn(vacancies);

        mockMvc.perform(MockMvcRequestBuilders.get("/vacancy"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Java Developer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Backend dev"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].position").value("Backend"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(150000))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].experience").value("3 years"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("Moscow"));
    }

    @Test
    void shouldReturnFilteredVacanciesByCity() throws Exception {
        List<Vacancy> vacancies = List.of(
                Vacancy.builder()
                        .id(3L)
                        .name("Frontend Dev")
                        .description("UI dev")
                        .position("Frontend")
                        .salary(BigDecimal.valueOf(120000))
                        .experience("2 years")
                        .city("Moscow")
                        .build()
        );

        Mockito.when(vacancyService.getVacancies(null, null, "Moscow"))
                .thenReturn(vacancies);

        mockMvc.perform(MockMvcRequestBuilders.get("/vacancy")
                        .param("city", "Moscow"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("Moscow"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].position").value("Frontend"));
    }

    @Test
    void shouldReturnEmptyListWhenNoVacanciesFound() throws Exception {
        Mockito.when(vacancyService.getVacancies("Nonexistent", null, null))
                .thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/vacancy")
                        .param("name", "Nonexistent"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnFilteredVacanciesByAllParams() throws Exception {
        List<Vacancy> vacancies = List.of(
                Vacancy.builder()
                        .id(5L)
                        .name("Middle Java Developer")
                        .description("Spring Boot, Hibernate")
                        .position("Backend")
                        .salary(BigDecimal.valueOf(200000))
                        .experience("5 years")
                        .city("Moscow")
                        .build()
        );

        Mockito.when(vacancyService.getVacancies("Middle Java Developer", "Backend", "Moscow"))
                .thenReturn(vacancies);

        mockMvc.perform(MockMvcRequestBuilders.get("/vacancy")
                        .param("name", "Middle Java Developer")
                        .param("position", "Backend")
                        .param("city", "Moscow"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Middle Java Developer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("Moscow"));
    }
}
