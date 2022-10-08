package com.naukma.aic.exceptionHandlers.exceptions;

public class FacultyNotFoundException extends RuntimeException {
    public FacultyNotFoundException(Long id) {
        super("Faculty " + id + " not found ");
    }
}
