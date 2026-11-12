package com.srms.exception;

/** Thrown when a roll number or email already exists in storage. */
public class DuplicateRecordException extends Exception {

    private static final long serialVersionUID = 1L;

    public DuplicateRecordException(String message) {
        super(message);
    }
}
