package be.kdg.rideservice.domain.exceptions;

public class InternalRideServiceException extends RuntimeException {
    public InternalRideServiceException(String message) {
        super(message);
    }

    public InternalRideServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
