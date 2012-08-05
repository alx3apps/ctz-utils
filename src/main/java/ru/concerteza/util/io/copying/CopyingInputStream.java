package ru.concerteza.util.io.copying;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Input stream transparent wrapper, copies all read data into additional copy stream
 *
 * @author alexey
 * Date: 11/20/11
 * @see FullCopyingInputStream
 * @see CopyingInputStreamTest
 */
public class CopyingInputStream extends InputStream {
    protected final InputStream source;
    protected final OutputStream copy;

    /**
     * @param source source input stream
     * @param copy stream to copy read bytes into, will NOT be closed on EOF or {@code close()}
     */
    public CopyingInputStream(InputStream source, OutputStream copy) {
        checkNotNull(source, "Provided source stream is null");
        checkNotNull(copy, "Provided copy stream is null");
        this.source = source;
        this.copy = copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        byte b[] = new byte[1];
        if (read(b, 0, 1) == -1) return -1;
        else return b[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int res = source.read(b, off, len);
        if(-1 != res) copy.write(b, off, res);
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        source.close();
    }
}
