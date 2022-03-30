package umcs.spotify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyTakenException extends BaseRuntimeException {

    public EmailAlreadyTakenException(String message, Object... replacements) {
        super(message, replacements);
    }

}
