package ru.concerteza.util.io.noclose;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/7/12
 */
public class NoCloseInputStream extends InputStream {
    private final InputStream target;

    public NoCloseInputStream(InputStream target) {
        checkNotNull(target, "Provided input stream is null");
        this.target = target;
    }

    public static NoCloseInputStream of(InputStream target) {
        return new NoCloseInputStream(target);
    }

    @Override
    public int read() throws IOException {
        return target.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return target.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        // NOOP
    }
}
