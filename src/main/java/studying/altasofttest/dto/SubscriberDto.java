package studying.altasofttest.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberDto {
    Long id;
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    String email;
    @NotBlank(message = "Name must not be empty")
    String fullName;
    String city;
    @NotBlank(message = "Desired position must not be empty")
    String desiredPosition;
}
