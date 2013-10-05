package ru.concerteza.util.io;

/**
 * Specific exception for runtime IO errors
 *
 * @author alexkasko
 * Date: 9/20/13
 */
public class CtzIoException extends RuntimeException {
    private static final long serialVersionUID = -843385113609473852L;

    public CtzIoException(String message) {
        super(message);
    }

    public CtzIoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CtzIoException(Throwable cause) {
        super(cause);
    }
}
