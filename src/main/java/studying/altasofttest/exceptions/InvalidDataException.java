package studying.altasofttest.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidDataException extends RuntimeException {


    private final Map<String, String> errors;

    public InvalidDataException(Map<String, String> errors) {
        super("Invalid data");
        this.errors = errors;
    }
}
