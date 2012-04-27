package ru.concerteza.util.db.blob;

import ru.concerteza.util.except.MessageException;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 4/14/12
 */
public class BlobException extends MessageException {

    public BlobException(Throwable cause, String template, Object... args) {
        super(format(template, args), cause);
    }
}
