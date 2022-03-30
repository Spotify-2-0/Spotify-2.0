package umcs.spotify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends BaseRuntimeException {

    public UserNotFoundException(String message, Object... replacements) {
        super(message, replacements);
    }

}
