package com.naukma.aic.exceptionHandlers.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String login) {
        super("Користувача з email/ipn: " + login + " не знайдено, або він не існує");
    }
}
