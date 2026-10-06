package seng202.team4.exceptions;

public class DatabaseAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
