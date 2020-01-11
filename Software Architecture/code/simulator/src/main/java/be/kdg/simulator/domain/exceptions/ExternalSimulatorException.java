package be.kdg.simulator.domain.exceptions;

public class ExternalSimulatorException extends RuntimeException {
    public ExternalSimulatorException(String message) {
        super(message);
    }

    public ExternalSimulatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
