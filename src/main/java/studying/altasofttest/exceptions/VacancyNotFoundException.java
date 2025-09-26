package studying.altasofttest.exceptions;

public class VacancyNotFoundException extends RuntimeException {
    public VacancyNotFoundException(Long id) {
        super("Vacancy with id=" + id + " not found");
    }
}



