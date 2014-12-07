package ru.concerteza.util.io.copying;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Reader transparent wrapper, copies all read data into additional copy writer
 *
 * @author alexey
 * Date: 12/5/11
 * @see FullCopyingReader
 * @see CopyingReaderTest
 */
public class CopyingReader extends Reader {
    protected final Reader source;
    protected final Writer copy;

    /**
     * @param source source reader
     * @param copy writer to copy read chars into, will NOT be closed on EOF or {@code close()}
     */
    public CopyingReader(Reader source, Writer copy) {
        this.source = source;
        this.copy = copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int res = source.read(cbuf, off, len);
        if (-1 != res) copy.write(cbuf, off, res);
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(source);
    }
}
