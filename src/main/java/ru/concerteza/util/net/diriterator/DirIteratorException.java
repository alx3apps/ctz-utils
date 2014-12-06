package ru.concerteza.util.net.diriterator;

/**
 * User: alexkasko
 * Date: 12/6/14
 */
public class DirIteratorException extends RuntimeException {
    private static final long serialVersionUID = 4404334947147476784L;

    public DirIteratorException(String message) {
        super(message);
    }

    public DirIteratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
