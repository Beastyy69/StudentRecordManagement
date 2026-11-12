package com.srms.exception;

/** Thrown when user input fails validation (empty field, bad email, etc.). */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
