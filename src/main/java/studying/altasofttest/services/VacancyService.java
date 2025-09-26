package studying.altasofttest.services;

import studying.altasofttest.dto.VacancyDto;
import studying.altasofttest.models.Vacancy;
import java.util.List;

public interface VacancyService {

    List<Vacancy> getVacancies(String name, String position, String city);

    VacancyDto createVacancy(VacancyDto vacancyDto);

    void deleteVacancy(Long id);

    VacancyDto getVacancy(Long id);

    VacancyDto editVacancy(VacancyDto vacancyDto);
}
