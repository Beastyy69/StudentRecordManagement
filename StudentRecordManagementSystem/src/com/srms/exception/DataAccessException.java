package com.srms.exception;

/**
 * Wraps low-level storage errors (SQLException, IOException) so the UI
 * only has to deal with one checked exception type.
 */
public class DataAccessException extends Exception {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
