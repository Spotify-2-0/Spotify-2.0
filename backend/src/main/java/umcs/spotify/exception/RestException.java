package umcs.spotify.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import umcs.spotify.helper.Formatter;

import java.util.Map;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RestException extends RuntimeException {

    private final HttpStatus status;
    private final Map<String, String> errors = Map.of();

    public RestException(HttpStatus status) {
        super();
        this.status = status;
    }

    public RestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public RestException(HttpStatus status, String message, Object... replacements) {
        super(Formatter.format(message, replacements));
        this.status = status;
    }

    public RestException(HttpStatus status, Map<String, String> errors, String message, Object... replacements) {
        super(Formatter.format(message, replacements));
        this.status = status;
    }

    public RestException(HttpStatus status, Map<String, String> errors) {
        super(status.getReasonPhrase());
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
