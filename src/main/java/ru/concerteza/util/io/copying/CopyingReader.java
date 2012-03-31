package ru.concerteza.util.io.copying;

import java.io.*;

/**
 * User: alexey
 * Date: 12/5/11
 */
public class CopyingReader extends Reader {
    protected final Reader source;
    // won't close it
    protected final Writer copy;

    public CopyingReader(Reader target, Writer copy) {
        this.source = target;
        this.copy = copy;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int res = source.read(cbuf, off, len);
        if (-1 != res) copy.write(cbuf, off, res);
        return res;
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}
