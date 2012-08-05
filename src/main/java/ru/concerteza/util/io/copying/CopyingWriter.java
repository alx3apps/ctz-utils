package ru.concerteza.util.io.copying;

import java.io.IOException;
import java.io.Writer;

/**
 * Writer transparent wrapper, copies all written data into additional copy writer
 *
 * @author alexey
 * Date: 8/5/12
 * @see CopyingWriterTest
 */
public class CopyingWriter extends Writer {
    private final Writer target;
    private final Writer copy;

    /**
     * @param target target writer to wrap
     * @param copy writer to copy written chars into, will NOT be closed on EOF or {@code close()}
     */
    public CopyingWriter(Writer target, Writer copy) {
        this.target = target;
        this.copy = copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        target.write(cbuf, off, len);
        copy.write(cbuf, off, len);
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
