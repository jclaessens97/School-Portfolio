package be.kdg.cluedoauth.controllers.api.advices;

import be.kdg.cluedoauth.dto.ApiError;
import be.kdg.cluedoauth.exceptions.AuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((err) -> {
            String fieldname = ((FieldError) err).getField();
            String errMessage = err.getDefaultMessage();
            errors.put(fieldname, errMessage);
            LOGGER.error(String.format("%s:\t%s", fieldname, errMessage));
        });

        return new ResponseEntity<>(errors, headers, status);
    }

    @ExceptionHandler(AuthException.class)
    protected ResponseEntity<Object> handleAuthException(AuthException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getAuthExceptionType().toString());
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }
}
