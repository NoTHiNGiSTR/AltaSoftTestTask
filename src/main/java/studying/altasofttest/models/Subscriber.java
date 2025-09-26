package studying.altasofttest.models;

import lombok.*;
import javax.persistence.*;

@Entity
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "subscribers")
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String fullName;
    private String city = "Не важно";
    @Column(nullable = false)
    private String desiredPosition;
}
