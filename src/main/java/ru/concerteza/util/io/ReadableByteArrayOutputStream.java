package ru.concerteza.util.io;

import java.io.ByteArrayOutputStream;

import static java.lang.System.arraycopy;

/**
 * {@link UnsyncByteArrayOutputStream} extension, tha allows direct read of stream buffer without {@code byte[]}
 * array creation on every read. Doesn't change original {@link ByteArrayOutputStream} logic.
 * Not thread-safe.
 *
 * @author alexey
 * Date: 8/28/12
 * @see UnsyncByteArrayOutputStream
 * @see ReadableByteArrayOutputStreamTest
 */
public class ReadableByteArrayOutputStream extends UnsyncByteArrayOutputStream /*, InputStream */ {
    private int readIndex = 0;

    /**
     * @return one byte, -s on error
     */
    public int read() {
        if(readIndex >= count) return -1;
        // convert signed byte to unsigned one as int
        int res = buf[readIndex] & (0xff);
        readIndex += 1;
        return res;
    }

    /**
     * Reads up to <code>len</code> bytes
     *
     * @param b buffer
     * @param off offset
     * @param len length
     * @return count of bytes read, -1 on no data
     */
    public int read(byte[] b, int off, int len) {
        if(readIndex >= count) return -1;
        int readLength = Math.min(len, count - readIndex);
        arraycopy(buf, readIndex, b, off, readLength);
        readIndex += readLength;
        return readLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        super.reset();
        readIndex = 0;
    }
}
