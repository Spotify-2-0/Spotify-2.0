package umcs.spotify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends BaseRuntimeException {

    public InvalidCredentialsException(String message, Object... replacements) {
        super(message, replacements);
    }

    public InvalidCredentialsException(Map<String, String> errors) {
        super(errors);
    }
}
