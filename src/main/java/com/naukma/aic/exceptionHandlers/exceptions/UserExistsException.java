package com.naukma.aic.exceptionHandlers.exceptions;

public class UserExistsException extends RuntimeException {
    public UserExistsException() {
        super("Користувач з ");
    }
}
