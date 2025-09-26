package studying.altasofttest.services.implementations;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studying.altasofttest.dto.VacancyDto;
import studying.altasofttest.exceptions.InvalidDataException;
import studying.altasofttest.exceptions.VacancyNotFoundException;
import studying.altasofttest.factories.VacancyFactory;
import studying.altasofttest.models.Vacancy;
import studying.altasofttest.repositories.VacancyRepository;
import studying.altasofttest.repositories.VacancySpecification;
import studying.altasofttest.services.VacancyService;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyFactory vacancyFactory;

    public VacancyServiceImpl(VacancyRepository vacancyRepository, VacancyFactory vacancyFactory) {
        this.vacancyRepository = vacancyRepository;
        this.vacancyFactory = vacancyFactory;
    }

    @Override
    public List<Vacancy> getVacancies(String name, String position, String city) {
        Specification<Vacancy> spec = Specification
                .where(VacancySpecification.hasName(name))
                .and(VacancySpecification.hasPosition(position))
                .and(VacancySpecification.hasCity(city));
        return vacancyRepository.findAll(spec);
    }

    @Transactional
    @Override
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyFactory.toEntity(vacancyDto);
        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return vacancyFactory.toDto(savedVacancy);
    }

    @Transactional
    @Override
    public void deleteVacancy(Long id) {
        try {
            vacancyRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new VacancyNotFoundException(id);
        }
    }

    @Transactional
    @Override
    public VacancyDto getVacancy(Long id) {
        Vacancy result = vacancyRepository.findById(id).orElseThrow(
                () -> new VacancyNotFoundException(id)
        );
        return vacancyFactory.toDto(result);
    }

    @Transactional
    @Override
    public VacancyDto editVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(vacancyDto.getId())
                .orElseThrow(() -> new VacancyNotFoundException(vacancyDto.getId()));
        checkValidationAndEditVacancy(vacancyDto, vacancy);
        vacancyRepository.save(vacancy);
        return vacancyFactory.toDto(vacancy);
    }


    private void checkValidationAndEditVacancy(VacancyDto vacancyDto, Vacancy vacancy){
        Map<String, String> errors = new LinkedHashMap<>();
        if (vacancyDto.getName() != null) {
            if (vacancyDto.getName().isBlank()) {
                errors.put("name", "Vacancy name must not be empty");
            } else {
                vacancy.setName(vacancyDto.getName());
            }
        }
        if (vacancyDto.getPosition() != null) {
            if (vacancyDto.getPosition().isBlank()) {
                errors.put("position", "Position must not be empty");
            } else {
                vacancy.setPosition(vacancyDto.getPosition());
            }
        }
        if (vacancyDto.getSalary() != null) {
            if (vacancyDto.getSalary().compareTo(BigDecimal.ZERO) < 0) {
                errors.put("salary", "Salary must be positive");
            } else {
                vacancy.setSalary(vacancyDto.getSalary());
            }
        }
        if (vacancyDto.getDescription() != null) {
            if (vacancyDto.getDescription().length() > 2000) {
                errors.put("description", "Description must not exceed 2000 characters");
            } else {
                vacancy.setDescription(vacancyDto.getDescription());
            }
        }
        if (vacancyDto.getExperience() != null && !vacancyDto.getExperience().isEmpty()){
                vacancy.setExperience(vacancyDto.getExperience());
        }

        if (vacancyDto.getCity() != null){
            if (vacancyDto.getCity().isEmpty()){
                errors.put("city", "City must not be empty");
            } else {
                vacancy.setCity(vacancyDto.getCity());
            }
        }

        if (!errors.isEmpty()) {
            throw new InvalidDataException(errors);
        }
    }
}
