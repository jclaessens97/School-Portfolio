package be.kdg.rideservice.controllers.api.advices;

import be.kdg.rideservice.domain.exceptions.ExternalRideServiceException;
import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.exceptions.VehicleNotFoundException;
import be.kdg.rideservice.dto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(InternalRideServiceException.class)
    protected ResponseEntity<Object> handleRideServiceException(InternalRideServiceException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }

    @ExceptionHandler(ExternalRideServiceException.class)
    protected ResponseEntity<Object> handleRideServiceException(ExternalRideServiceException ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    protected ResponseEntity<Object> handleVehicleNotFoundException(VehicleNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }
}
