package ru.concerteza.util.db.postgrescopy;

/**
 * Specific exceptions for postgres copy errors
 *
 * @author alexkasko
 * Date: 5/5/13
 */
public class PostgresCopyException extends RuntimeException {
    private static final long serialVersionUID = 4930089087427080205L;

    /**
     * Constructor
     *
     * @param cause cause throwable
     */
    public PostgresCopyException(Throwable cause) {
        super(cause);
    }
}
