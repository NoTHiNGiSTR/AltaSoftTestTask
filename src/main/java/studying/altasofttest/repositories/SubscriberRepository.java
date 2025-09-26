package studying.altasofttest.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import studying.altasofttest.models.Subscriber;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    @Override
    Page<Subscriber> findAll(Pageable pageable);

    boolean existsByEmail(@Param("email") String email);

}
