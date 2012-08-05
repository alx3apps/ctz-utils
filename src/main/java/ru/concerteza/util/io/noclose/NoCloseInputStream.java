package ru.concerteza.util.io.noclose;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Input stream transparent wrapper, {@link java.io.InputStream#close()} overriden as NOOP
 *
 * @author alexey
 * Date: 7/7/12
 */
public class NoCloseInputStream extends InputStream {
    private final InputStream target;

    /**
     * @param target target stream
     */
    public NoCloseInputStream(InputStream target) {
        checkNotNull(target, "Provided input stream is null");
        this.target = target;
    }

    /**
     * Factory method
     *
     * @param target target stream
     * @return NoCloseInputStream instance
     */
    public static NoCloseInputStream of(InputStream target) {
        return new NoCloseInputStream(target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        return target.read();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return target.read(b, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // NOOP
    }
}
