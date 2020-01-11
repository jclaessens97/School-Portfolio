package be.kdg.sensorservice.domain.exceptions;

public class InternalSensorServiceException extends RuntimeException {
    public InternalSensorServiceException(String message) {
        super(message);
    }

    public InternalSensorServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
