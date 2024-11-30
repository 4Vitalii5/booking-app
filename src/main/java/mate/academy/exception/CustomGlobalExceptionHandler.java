package mate.academy.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
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
