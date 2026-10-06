package seng202.team4.exceptions;

/**
 * Custom DuplicateEntryException to be thrown if a db record creation is attempted with a duplicate key
 * @author Morgan English
 */
public class DuplicateEntryException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Simple constructor that passes to parent Exception class with throwable
     * @param message error message
     * @param cause causing exception
     */
    public DuplicateEntryException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Simple constructor that passes to parent Exception class
     * @param message error message
     */
    public DuplicateEntryException(String message) {
        super(message);
    }
}
