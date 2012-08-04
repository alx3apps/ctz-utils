package ru.concerteza.util.db.blob.compress;

import ru.concerteza.util.except.MessageException;

/**
 * * Exception for compression related errors
 *
 * @author alexey
 * Date: 4/28/12
 * @see Compressor
 */
public class CompressException extends MessageException {
    /**
     * @param message error message
     * @param cause error cause
     */
    public CompressException(String message, Throwable cause) {
        super(message, cause);
    }
}
