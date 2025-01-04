package org.cyberrealm.tech.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BookingForbiddenException.class)
    protected ResponseEntity<Object> handleBookingForbiddenException(
            BookingForbiddenException exception, WebRequest request) {
        Map<String, Object> body = getBody(
                exception.getMessage(),
                request.getDescription(false),
                HttpStatus.FORBIDDEN
        );
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PaymentProcessingException.class)
    protected ResponseEntity<Object> handlePaymentProcessingException(
            PaymentProcessingException exception, WebRequest request) {
        Map<String, Object> body = getBody(
                exception.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StripeProcessingException.class)
    protected ResponseEntity<Object> handleStripeProcessingException(
            StripeProcessingException exception, WebRequest request) {
        Map<String, Object> body = getBody(
                exception.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingProcessingException.class)
    protected ResponseEntity<Object> handleBookingProcessingException(
            BookingProcessingException exception, WebRequest request) {
        Map<String, Object> body = getBody(
                exception.getMessage(),
                request.getDescription(false),
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistrationException(
            RegistrationException exception, WebRequest request) {
        Map<String, Object> body = getBody(
                exception.getMessage(),
                request.getDescription(false),
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException exception, WebRequest request) {
        Map<String, Object> body = getBody(
                exception.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    protected ResponseEntity<Object> handleDuplicateResourceException(
            DuplicateResourceException exception, WebRequest request
    ) {
        Map<String, Object> body = getBody(
                exception.getMessage(),
                request.getDescription(false),
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    private String getErrorMessage(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            String field = fieldError.getField();
            String message = error.getDefaultMessage();
            return "%s : %s".formatted(field, message);
        }
        return error.getDefaultMessage();
    }

    private Map<String, Object> getBody(
            String message,
            String description,
            HttpStatus httpStatus
    ) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", httpStatus);
        body.put("message", message);
        body.put("description", description);
        return body;
    }
}
