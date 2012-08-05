package ru.concerteza.util.io.noclose;

import java.io.IOException;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Writer transparent wrapper, {@link java.io.Writer#close()}
 * and {@link java.io.Writer#flush()} overriden as NOOP
 *
 * @author alexey
 * Date: 7/7/12
 */
public class NoCloseWriter extends Writer {
    private final Writer target;

    /**
     * @param target target writer
     */
    public NoCloseWriter(Writer target) {
        checkNotNull(target, "Provided writer is null");
        this.target = target;
    }

    /**
     * Factory method
     *
     * @param target target writer
     * @return NoCloseWriter instance
     */
    public static NoCloseWriter of(Writer target) {
        return new NoCloseWriter(target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        target.write(cbuf, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        // NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // NOOP
    }
}
