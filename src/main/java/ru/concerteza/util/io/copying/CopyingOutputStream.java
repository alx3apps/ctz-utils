package ru.concerteza.util.io.copying;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream transparent wrapper, copies all written data into additional copy stream
 *
 * @author alexey
 * Date: 8/5/12
 * @see CopyingOutputStreamTest
 */
public class CopyingOutputStream extends OutputStream {
    private final OutputStream target;
    private final OutputStream copy;

    /**
     * @param target target stream to wrap
     * @param copy stream to copy written bytes into, will NOT be closed on EOF or {@code close()}
     */
    public CopyingOutputStream(OutputStream target, OutputStream copy) {
        this.target = target;
        this.copy = copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException {
        target.write(b);
        copy.write(b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        target.write(b, off, len);
        copy.write(b, off, len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        target.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        target.close();
    }
}
