package ro.deloittedigital.samekh.usermanagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ro.deloittedigital.samekh.usermanagement.exception.FieldAlreadyUsedException;
import ro.deloittedigital.samekh.usermanagement.exception.IncorrectCredentialsException;
import ro.deloittedigital.samekh.usermanagement.exception.UserNotFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<List<String>> handleHttpMessageNotReadableException() {
        List<String> errors = List.of("The request body is not valid.");
        log.info("[ExceptionHandlerController] HTTP message not readable: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> errors = new ArrayList<>();
        exception.getConstraintViolations().forEach(constraintViolation -> {
            String field = null;
            for (Node node : constraintViolation.getPropertyPath()) {
                field = node.getName();
            }
            errors.add(String.format("The %s: '%s' %s.", field, constraintViolation.getInvalidValue(),
                    constraintViolation.getMessage()));
        });
        log.info("[ExceptionHandlerController] constraint violations: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = IncorrectCredentialsException.class)
    public ResponseEntity<List<String>> handleIncorrectCredentials(IncorrectCredentialsException exception) {
        List<String> errors = List.of(exception.getMessage());
        log.info("[ExceptionHandlerController] incorrect credentials: {}", errors);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<List<String>> handleUserNotFoundException(UserNotFoundException exception) {
        List<String> errors = List.of(exception.getMessage());
        log.info("[ExceptionHandlerController] user not found: {}", errors);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(value = FieldAlreadyUsedException.class)
    public ResponseEntity<List<String>> handleEmailAlreadyUsedException(FieldAlreadyUsedException exception) {
        List<String> errors = List.of(exception.getMessage());
        log.info("[ExceptionHandlerController] field already used: {}", errors);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }
}
