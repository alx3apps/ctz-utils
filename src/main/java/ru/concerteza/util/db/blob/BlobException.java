package ru.concerteza.util.db.blob;

import ru.concerteza.util.except.MessageException;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * Exception for BLOB related errors
 *
 * @author alexey
 * Date: 4/14/12
 * @see ReadableBlob
 * @see WritableBlob
 * @see DetachedBlob
 */
public class BlobException extends MessageException {

    /**
     * @param cause cause exception
     * @param template message template
     * @param args message arguments
     */
    public BlobException(Throwable cause, String template, Object... args) {
        super(format(template, args), cause);
    }
}
