package studying.altasofttest.factories;

import org.springframework.stereotype.Component;
import studying.altasofttest.dto.VacancyDto;
import studying.altasofttest.models.Vacancy;

@Component
public class VacancyFactory {
    public VacancyDto toDto(Vacancy vacancy){
        return VacancyDto.builder()
                .id(vacancy.getId())
                .name(vacancy.getName())
                .description(vacancy.getDescription())
                .position(vacancy.getPosition())
                .salary(vacancy.getSalary())
                .experience(vacancy.getExperience())
                .city(vacancy.getCity())
                .build();
    }

    public Vacancy toEntity(VacancyDto vacancyDto){
        return Vacancy.builder()
                .name(vacancyDto.getName())
                .description(vacancyDto.getDescription())
                .position(vacancyDto.getPosition())
                .salary(vacancyDto.getSalary())
                .experience(vacancyDto.getExperience())
                .city(vacancyDto.getCity())
                .build();
    }
}
