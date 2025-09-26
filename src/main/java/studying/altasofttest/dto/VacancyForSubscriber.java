package studying.altasofttest.dto;

import studying.altasofttest.models.Subscriber;
import studying.altasofttest.models.Vacancy;

import java.util.List;

public record VacancyForSubscriber(Subscriber subscriber, List<Vacancy> vacancies) {}
