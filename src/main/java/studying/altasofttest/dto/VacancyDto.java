package studying.altasofttest.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class VacancyDto {
    Long id;
    @NotBlank(message = "Vacancy name must not be empty")
    private String name;
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    @NotBlank(message = "Position must not be empty")
    private String position;
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be positive")
    private BigDecimal salary;
    private String experience;
    @NotBlank(message = "City must not be empty")
    private String city;
}
