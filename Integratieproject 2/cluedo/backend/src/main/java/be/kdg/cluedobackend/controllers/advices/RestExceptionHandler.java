package be.kdg.cluedobackend.controllers.advices;

import be.kdg.cluedobackend.dto.ApiError;
import be.kdg.cluedobackend.exceptions.CluedoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(CluedoException.class)
    protected ResponseEntity<Object> handleCluedoException(CluedoException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getCluedoExceptionType().toString());
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }



}
