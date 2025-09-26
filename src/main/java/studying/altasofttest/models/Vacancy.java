package studying.altasofttest.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "vacancies")
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(length = 2000)
    private String description;
    @Column(nullable = false)
    private String position;
    private BigDecimal salary;
    private String experience;
    private String city;
    @CreationTimestamp
    private Timestamp createdAt;
}
