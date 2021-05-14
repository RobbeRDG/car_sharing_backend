package be.kul.billingservice.utils.exceptions;

import be.kul.billingservice.utils.json.jsonObjects.rest.ErrorMessage;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerConfig extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {
            NotAllowedException.class,
            DoesntExistException.class,
            StripeException.class
    })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getLocalizedMessage();

        //Create the error message
        ErrorMessage errorMessage = new ErrorMessage(bodyOfResponse);

        //Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Exception-Type", String.valueOf(ex.getClass()));

        //Return to client
        log.warn("Handled conflict: " + bodyOfResponse);
        return handleExceptionInternal(
                ex,
                errorMessage,
                headers,
                HttpStatus.CONFLICT, request
        );
    }

    @ExceptionHandler(value = {}) //Handle all unexpected exceptions
    protected ResponseEntity<Object> handleSomethingWentWrong(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getLocalizedMessage();

        //Create the error message
        ErrorMessage errorMessage = new ErrorMessage(bodyOfResponse);

        //Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Exception-Type", String.valueOf(ex.getClass()));

        ex.printStackTrace();

        log.warn("Handled conflict: " + bodyOfResponse);

        //Return to client
        return handleExceptionInternal(
                ex,
                errorMessage,
                headers,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }
}
