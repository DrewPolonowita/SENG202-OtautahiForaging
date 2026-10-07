package seng202.team4.exceptions;

import java.io.Serial;

/**
 * Runtime exception for when a database operation doesn't work
 */
public class DatabaseAccessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param message error message
     * @param cause error cause
     */
    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
