package ru.concerteza.util.io;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.db.blob.compress.Compressor;
import ru.concerteza.util.db.blob.compress.NoCompressor;

import java.io.*;

import static com.google.common.base.Preconditions.checkNotNull;
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
    private final Function<TempFile, Void> fun;
    private final Compressor compressor;
    private final File file;
    private final CountingOutputStream out;
    private final CountingOutputStream compressedOut;
    private TempFileInputStream in;

    public TempFileOutputInputStream(Function<TempFile, Void> fun) {
        this(fun, new NoCompressor());
    }

    public TempFileOutputInputStream(Function<TempFile, Void> fun, Compressor compressor) {
        try {
            checkNotNull(fun);
            checkNotNull(compressor);
            this.fun = fun;
            this.compressor = compressor;
            this.file = File.createTempFile(getClass().getName(), ".tmp");
            this.file.deleteOnExit(); // just in case
            FileOutputStream fileOut = new FileOutputStream(this.file);
            BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut);
            this.compressedOut = new CountingOutputStream(bufferedOut);
            OutputStream wrappedOut = compressor.wrapCompress(compressedOut);
            this.out = new CountingOutputStream(wrappedOut);
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
        TempFile args = new TempFile(in, compressor, out.getByteCount(), compressedOut.getByteCount());
        // do work here
        fun.apply(args);
    }

    public InputStream inputStream() {
        checkState(null != in, "InputStream is not ready, OutputStream wasn't closed");
        return in;
    }

    public static class TempFile {
        private final InputStream inputStream;
        private final Compressor compressor;
        private final long decompressedLength;
        private final long compressedLength;

        public TempFile(InputStream inputStream, Compressor compressor, long decompressedLength, long compressedLength) {
            this.inputStream = inputStream;
            this.compressor = compressor;
            this.decompressedLength = decompressedLength;
            this.compressedLength = compressedLength;
        }

        public InputStream getCompressed() {
            return inputStream;
        }

        public InputStream getDecompressed() {
            return compressor.wrapDecompress(inputStream);
        }

        public long getDecompressedLength() {
            return decompressedLength;
        }

        public long getCompressedLength() {
            return compressedLength;
        }
    }
}
