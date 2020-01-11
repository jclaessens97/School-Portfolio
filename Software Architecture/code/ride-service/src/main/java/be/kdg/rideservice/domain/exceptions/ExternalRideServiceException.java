package be.kdg.rideservice.domain.exceptions;

public class ExternalRideServiceException extends RuntimeException {
    public ExternalRideServiceException(String message) {
        super(message);
    }

    public ExternalRideServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
