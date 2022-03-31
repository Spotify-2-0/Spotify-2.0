package umcs.spotify.helper;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

public final class FormValidatorHelper {

    public static Map<String, String> returnFormattedErrors(Errors errors) {
        return errors.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage
                ));
    }
}
