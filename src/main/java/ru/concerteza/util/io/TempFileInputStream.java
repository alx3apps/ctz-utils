package ru.concerteza.util.io;

import org.apache.commons.lang.UnhandledException;

import java.io.*;

/**
 * User: alexey
 * Date: 4/13/12
 */
@Deprecated // not useful
public class TempFileInputStream extends InputStream {
    private final File file;
    private final FileInputStream is;

    public TempFileInputStream(File file) {
        try {
            this.file = file;
            is = new FileInputStream(file);
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        is.close();
        file.delete();
    }
}
