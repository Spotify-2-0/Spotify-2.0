package umcs.spotify.exception;

import umcs.spotify.helper.Formatter;

import java.util.Map;

public class BaseRuntimeException extends RuntimeException {

    private Map<String, String> formErrors;

    public BaseRuntimeException(String message, Object... replacements) {
        super(Formatter.format(message, replacements));
    }

    public BaseRuntimeException(Map<String, String> formErrors) {
        this.formErrors = formErrors;
    }

    public Map<String, String> getErrors() {
        return formErrors;
    }

}
