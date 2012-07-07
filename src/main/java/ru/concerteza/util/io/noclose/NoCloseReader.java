package ru.concerteza.util.io.noclose;

import java.io.IOException;
import java.io.Reader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/7/12
 */
public class NoCloseReader extends Reader {
    private final Reader target;

    public NoCloseReader(Reader target) {
        checkNotNull(target, "Provided reader is null");
        this.target = target;
    }

    public static NoCloseReader of(Reader target) {
        return new NoCloseReader(target);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return target.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
        // NOOP
    }
}
