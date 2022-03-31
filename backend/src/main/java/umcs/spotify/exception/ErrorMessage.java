package umcs.spotify.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import java.util.Map;


@Value
public class ErrorMessage {

    int statusCode;
    long timestamp;
    String message;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    Map<String, String> formErrors;

}