package be.kul.carservice.utils;

import be.kul.carservice.cars.service.CarService;
import be.kul.carservice.utils.exceptions.AlreadyExistsException;
import be.kul.carservice.utils.exceptions.DoesntExistException;
import be.kul.carservice.utils.exceptions.NotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class ExceptionHandlerConfig extends ResponseEntityExceptionHandler {
    Logger logger = LoggerFactory.getLogger(CarService.class);

    @ExceptionHandler(value = {
            AlreadyExistsException.class,
            DoesntExistException.class,
            NotAvailableException.class
    })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        logger.warn(bodyOfResponse);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {

    })
    protected ResponseEntity<Object> handleSomethingWentWrong(RuntimeException ex, WebRequest request) {
        ex.printStackTrace();
        String bodyOfResponse = ex.getMessage();
        logger.error(bodyOfResponse);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
