package ru.concerteza.util.jni;

/**
 * Specific exception for JNI libraries load errors
 *
 * User: alexkasko
 * Date: 12/17/13
 */
public class CtzJniException extends RuntimeException {
    private static final long serialVersionUID = 4958497408618642582L;

    public CtzJniException(String message) {
        super(message);
    }

    public CtzJniException(String message, Throwable cause) {
        super(message, cause);
    }

    public CtzJniException(Throwable cause) {
        super(cause);
    }
}
