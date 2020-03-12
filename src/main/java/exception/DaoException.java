package exception;

/**
 * Exception type for database access errors.
 */
public class DaoException extends RuntimeException {
    /**
     * Instantiates a new DAO exception.
     *
     * @param message exception message
     * @param cause   exception that caused the DAO error
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
