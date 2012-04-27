package ru.concerteza.util.io;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.UnhandledException;

import java.io.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * User: alexey
 * Date: 4/13/12
 */

// created to solve OutputStream -> InputStream problem with temporary file. Use with caution.
// proper (complex) ways:
// http://ostermiller.org/convert_java_outputstream_inputstream.html
// http://code.google.com/p/io-tools/wiki/Tutorial_EasyStream
public class TempFileOutputInputStream extends OutputStream {
    private final Function<InputStream, Void> fun;
    private final File file;
    private final FileOutputStream out;
    private TempFileInputStream in;

    public TempFileOutputInputStream(Function<InputStream, Void> fun) {
        try {
            this.fun = fun;
            this.file = File.createTempFile(getClass().getName(), ".tmp");
            this.file.deleteOnExit(); // just in case
            this.out = new FileOutputStream(this.file);
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
        in = new TempFileInputStream(file);
        // do work here
        fun.apply(in);
    }

    public InputStream inputStream() {
        checkState(null != in, "InputStream is not ready, OutputStream wasn't closed");
        return in;
    }
}
