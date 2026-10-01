package seng202.team4.exceptions;

public class DatabaseAccessException extends RuntimeException {
    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
