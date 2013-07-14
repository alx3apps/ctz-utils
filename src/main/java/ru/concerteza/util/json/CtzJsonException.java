package ru.concerteza.util.json;

/**
 * Exception class for JSON related errors
 *
 * @author alexkasko
 * Date: 7/12/13
 */
public class CtzJsonException extends RuntimeException {
    private static final long serialVersionUID = -7137775414604544491L;

    public CtzJsonException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public CtzJsonException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public CtzJsonException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
