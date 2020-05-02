package be.kdg.cluedobackend.exceptions;

import lombok.Getter;

public class CluedoException extends Exception {
    @Getter
    private CluedoExceptionType cluedoExceptionType;

    public CluedoException(CluedoExceptionType cluedoExceptionType) {
        super(cluedoExceptionType.getMessage());
        this.cluedoExceptionType = cluedoExceptionType;
    }

    public CluedoException(CluedoExceptionType cluedoExceptionType, String message)
    {
        super(message);
        this.cluedoExceptionType = cluedoExceptionType;
    }
}
