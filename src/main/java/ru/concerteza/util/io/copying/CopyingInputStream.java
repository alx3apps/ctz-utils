package ru.concerteza.util.io.copying;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: alexey
 * Date: 11/20/11
 */
public class CopyingInputStream extends InputStream {
    protected final InputStream source;
    // won't close it
    protected final OutputStream copy;

    public CopyingInputStream(InputStream source, OutputStream copy) {
        this.source = source;
        this.copy = copy;
    }

    @Override
    public int read() throws IOException {
        byte b[] = new byte[1];
        if (read(b, 0, 1) == -1) return -1;
        else return b[0];
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int res = source.read(b, off, len);
        if(-1 != res) copy.write(b, off, res);
        return res;
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}
