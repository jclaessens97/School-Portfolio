package be.kdg.simulator.domain.exceptions;

/**
 * Custom exception that is used to pack & rethrow
 */
public class InternalSimulatorException extends RuntimeException {
    public InternalSimulatorException(String message) {
        super(message);
    }

    public InternalSimulatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
