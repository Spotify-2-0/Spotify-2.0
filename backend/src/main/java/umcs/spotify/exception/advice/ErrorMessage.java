package umcs.spotify.exception.advice;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorMessage {

    private int statusCode;
    private LocalDateTime timestamp;
    private String message;
    private Map<String, String> formErrors;

    public ErrorMessage(int statusCode, LocalDateTime timestamp, String message, Map<String, String> formErrors) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.formErrors = formErrors;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getFormErrors() {
        return formErrors;
    }

    public void setFormErrors(Map<String, String> formErrors) {
        this.formErrors = formErrors;
    }
}