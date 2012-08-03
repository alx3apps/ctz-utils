package ru.concerteza.util.db.blob.compress;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * No-op BLOB compressor implementation, returns provided streams untouched
 *
 * @author alexey
 * Date: 4/14/12
 * @see SnappyCompressor
 * @see GzipCompressor
 * @see XzCompressor
 * @see ru.concerteza.util.db.blob.tool.BlobTool
 */
public class NoCompressor implements Compressor {
    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream wrapCompress(OutputStream out) {
        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream wrapDecompress(InputStream in) {
        return in;
    }
}
