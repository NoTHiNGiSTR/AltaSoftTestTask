package studying.altasofttest.repositories;

import org.springframework.data.jpa.domain.Specification;
import studying.altasofttest.models.Vacancy;

public class VacancySpecification {

    public static Specification<Vacancy> hasName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Vacancy> hasPosition(String position) {
        return (root, query, cb) -> position == null ? null : cb.equal(cb.lower(root.get("position")), position.toLowerCase());
    }

    public static Specification<Vacancy> hasCity(String city) {
        return (root, query, cb) -> city == null ? null : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }
}
