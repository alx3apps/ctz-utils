package ru.concerteza.util.io.noclose;

import java.io.IOException;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/7/12
 */
public class NoCloseWriter extends Writer {
    private final Writer target;

    public NoCloseWriter(Writer target) {
        checkNotNull(target, "Provided writer is null");
        this.target = target;
    }

    public static NoCloseWriter of(Writer target) {
        return new NoCloseWriter(target);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        target.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        // NOOP
    }

    @Override
    public void close() throws IOException {
        // NOOP
    }
}
