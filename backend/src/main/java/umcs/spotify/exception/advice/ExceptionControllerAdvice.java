package umcs.spotify.exception.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import umcs.spotify.exception.BaseRuntimeException;
import umcs.spotify.exception.InvalidCredentialsException;
import umcs.spotify.exception.UserNotFoundException;
import umcs.spotify.helper.AnnotationHelper;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler({
        UserNotFoundException.class,
        InvalidCredentialsException.class
    })
    public ResponseEntity<ErrorMessage> handleNotFound(BaseRuntimeException ex, WebRequest request) {


        var status = AnnotationHelper.getAnnotation(
            ex.getClass(),
            ResponseStatus.class
        ).code();

        var errorMessage = new ErrorMessage(
            status.value(),
            LocalDateTime.now(),
            ex.getMessage(),
            ex.getErrors()
        );

        return new ResponseEntity<>(errorMessage, status);
    }

}