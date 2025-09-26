package studying.altasofttest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDto {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Object details;
}
