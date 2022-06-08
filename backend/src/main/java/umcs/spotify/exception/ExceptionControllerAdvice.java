package umcs.spotify.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@ControllerAdvice
public class ExceptionControllerAdvice extends ExceptionHandlerExceptionResolver {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(RestException ex, WebRequest request) {

        var errorMessage = new ErrorMessage(
            ex.getStatus().value(),
            System.currentTimeMillis(),
            ex.getMessage(),
            ex.getErrors()
        );

        return new ResponseEntity<>(errorMessage, ex.getStatus());
    }

}