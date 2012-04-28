package ru.concerteza.util.db.blob.compress;

import ru.concerteza.util.except.MessageException;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 4/28/12
 */
public class CompressException extends MessageException {
    public CompressException(String message, Throwable cause) {
        super(message, cause);
    }
}
