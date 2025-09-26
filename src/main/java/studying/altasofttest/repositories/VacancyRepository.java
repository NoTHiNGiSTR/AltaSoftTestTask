package studying.altasofttest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import studying.altasofttest.models.Vacancy;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long>, JpaSpecificationExecutor<Vacancy> {

    @Query("""
        SELECT v
        FROM Vacancy v
        WHERE v.position IN :positions
          AND v.city IN :cities
          AND v.createdAt > :since
        ORDER BY v.createdAt DESC
    """)
    List<Vacancy> findNewVacanciesForSubscribers(
            @Param("positions") List<String> positions,
            @Param("cities") List<String> cities,
            @Param("since") Timestamp since
    );

}