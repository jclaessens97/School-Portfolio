package be.kdg.cluedoauth.exceptions;

import lombok.Getter;

public class AuthException extends Exception {
    @Getter
    private AuthExceptionType authExceptionType;

    public AuthException(AuthExceptionType authExceptionType) {
        super(authExceptionType.getMessage());
        this.authExceptionType = authExceptionType;
    }
}
