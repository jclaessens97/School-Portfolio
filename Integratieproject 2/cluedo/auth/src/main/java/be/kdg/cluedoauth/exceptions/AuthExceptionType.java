package be.kdg.cluedoauth.exceptions;

import lombok.Getter;

public enum AuthExceptionType {
    USERNAME_EXISTS("User with username or email  already exists."),
    EMAIL_EXISTS("User with username or email  already exists."),
    PASSWORD_NOT_VALID("The password must contain at least 1 uppercase character and 1 digit."),
    USERID_DOES_NOT_EXIST("User with userid does not exists"),
    PASSWORD_IS_SAME("The password must be a different one to change it."),
    WRONG_PASSWORD("The old password is incorrect.");

    @Getter
    private final String message;

    AuthExceptionType(String message) {
        this.message = message;
    }
}
