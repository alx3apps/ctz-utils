package ru.concerteza.util.io;

import ru.concerteza.util.except.MessageException;

/**
 * User: alexey
 * Date: 5/3/12
 */
public class RuntimeIOException extends MessageException {
    public RuntimeIOException(String message) {
        super(message);
    }

    public RuntimeIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeIOException(Throwable cause) {
        super(cause);
    }
}
