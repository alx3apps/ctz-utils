package ru.concerteza.util.freemarker;

/**
 * Specific exception for Freemarker errors
 *
 * @author alexkasko
 * Date: 10/27/13
 */
public class FreemarkerException extends RuntimeException {
    private static final long serialVersionUID = -2750869123146824939L;

    public FreemarkerException(String message) {
        super(message);
    }

    public FreemarkerException(String message, Throwable cause) {
        super(message, cause);
    }

    public FreemarkerException(Throwable cause) {
        super(cause);
    }
}
