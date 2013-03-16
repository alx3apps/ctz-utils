package ru.concerteza.util.io;

import ru.concerteza.util.except.MessageException;

import static org.apache.commons.lang.StringUtils.defaultString;

/**
 * User: alexey
 * Date: 5/3/12
 */
@Deprecated // use business exceptions
public class RuntimeIOException extends MessageException {
    public RuntimeIOException(String message) {
        super(message);
    }

    public RuntimeIOException(String message, Throwable cause) {
        super(message, cause);
    }
    public RuntimeIOException(Exception cause) {
        super(cause, defaultString(cause.getMessage(), "No message available"));
    }
}
