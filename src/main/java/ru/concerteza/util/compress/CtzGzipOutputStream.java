package ru.concerteza.util.compress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import static java.lang.System.arraycopy;

/**
 * GZIP output stream extension that allow to reset the stream state
 *
 * User: alexkasko
 * Date: 10/21/14
 */
public class CtzGzipOutputStream extends GZIPOutputStream {
    // copied from parent
    private final static int GZIP_MAGIC = 0x8b1f;

    private final byte[] empty;

    /**
     * Constructor
     *
     * @param out target stream
     * @throws IOException
     */
    public CtzGzipOutputStream(OutputStream out) throws IOException {
        super(out);
        empty = new byte[buf.length];
    }

    /**
     * Constructor
     *
     * @param out target stream
     * @param size buffer size
     * @throws IOException
     */
    public CtzGzipOutputStream(OutputStream out, int size) throws IOException {
        super(out, size);
        empty = new byte[buf.length];
    }

    /**
     * Resets stream state
     *
     * @throws IOException
     */
    public void reset() throws IOException {
        // close
        finish();
        // reset
        crc.reset();
        def.reset();
        arraycopy(empty, 0, buf, 0, empty.length);
        // start
        writeHeader();
    }

    /*
     * Writes GZIP member header.
     */
    private void writeHeader() throws IOException {
        out.write(new byte[]{
                (byte) GZIP_MAGIC,        // Magic number (short)
                (byte) (GZIP_MAGIC >> 8),  // Magic number (short)
                Deflater.DEFLATED,        // Compression method (CM)
                0,                        // Flags (FLG)
                0,                        // Modification time MTIME (int)
                0,                        // Modification time MTIME (int)
                0,                        // Modification time MTIME (int)
                0,                        // Modification time MTIME (int)
                0,                        // Extra flags (XFLG)
                0                         // Operating system (OS)
        });
    }
}
