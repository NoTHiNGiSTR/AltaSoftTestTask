package studying.altasofttest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studying.altasofttest.dto.VacancyDto;
import studying.altasofttest.models.Vacancy;
import studying.altasofttest.services.VacancyService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping("/vacancy")
    public ResponseEntity<List<Vacancy>> getVacancies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String city) {
        return ResponseEntity.ok(vacancyService.getVacancies(name, position, city));
    }

    @GetMapping("/vacancy/{id}")
    public ResponseEntity<VacancyDto> getVacancyById(@PathVariable("id") Long id){
        VacancyDto vacancy = vacancyService.getVacancy(id);
        return ResponseEntity.ok().body(vacancy);
    }

    @PutMapping("/vacancy")
    public ResponseEntity<VacancyDto> createVacancy(@Valid @RequestBody VacancyDto vacancyDto){
        VacancyDto vacancy = vacancyService.createVacancy(vacancyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancy);
    }

    @DeleteMapping("/vacancy/{id}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable("id") Long id){
        vacancyService.deleteVacancy(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/vacancy")
    public ResponseEntity<VacancyDto> editVacancy(@RequestBody VacancyDto vacancyDto){
        return ResponseEntity.ok().body(vacancyService.editVacancy(vacancyDto));
    }
}
