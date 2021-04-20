package be.kul.carservice.utils.exceptions;

import be.kul.carservice.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    Logger logger = LoggerFactory.getLogger(CarService.class);

    @ExceptionHandler(value = {
            AlreadyExistsException.class,
            DoesntExistException.class,
            NotAvailableException.class,
            ReservationCooldownException.class,
            CarOfflineException.class,
            NotAllowedException.class
    })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Exception-Type", String.valueOf(ex.getClass()));

        log.warn(bodyOfResponse);

        return handleExceptionInternal(
                ex,
                bodyOfResponse,
                headers,
                HttpStatus.CONFLICT, request
        );
    }

    @ExceptionHandler(value = {}) //Handle all unexpected exceptions
    protected ResponseEntity<Object> handleSomethingWentWrong(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Exception-Type", String.valueOf(ex.getClass()));

        logger.error(bodyOfResponse);
        ex.printStackTrace();

        return handleExceptionInternal(
                ex,
                bodyOfResponse,
                headers,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }
}

