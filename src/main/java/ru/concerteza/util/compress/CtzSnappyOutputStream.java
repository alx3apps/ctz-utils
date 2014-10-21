package ru.concerteza.util.compress;

import org.iq80.snappy.SnappyOutputStream;
import ru.concerteza.util.io.noclose.NoFlushOutputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * User: alexkasko
 * Date: 10/21/14
 */
public class CtzSnappyOutputStream extends SnappyOutputStream {

    // copied from parent
    private static final byte[] STREAM_HEADER = new byte[] { 's', 'n', 'a', 'p', 'p', 'y', 0};
    private final OutputStream ctzOut;

    /**
     * Creates a Snappy output stream to write data to the specified underlying output stream.
     *
     * @param out the underlying output stream
     */
    public CtzSnappyOutputStream(OutputStream out) throws IOException {
        super(new NoFlushOutputStream(out));
        this.ctzOut = out;
    }

    public void reset() throws IOException {
        super.flush();
        ctzOut.write(STREAM_HEADER);
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        ctzOut.flush();
    }
}
