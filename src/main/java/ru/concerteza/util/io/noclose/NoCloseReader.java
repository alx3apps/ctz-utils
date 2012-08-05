package ru.concerteza.util.io.noclose;

import java.io.IOException;
import java.io.Reader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reader transparent wrapper, {@link java.io.Reader#close()} overriden as NOOP
 *
 * @author alexey
 * Date: 7/7/12
 */
public class NoCloseReader extends Reader {
    private final Reader target;

    /**
     * @param target target reader
     */
    public NoCloseReader(Reader target) {
        checkNotNull(target, "Provided reader is null");
        this.target = target;
    }

    /**
     * Factory method
     *
     * @param target target reader
     * @return NoCloseReader instance
     */
    public static NoCloseReader of(Reader target) {
        return new NoCloseReader(target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return target.read(cbuf, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // NOOP
    }
}
