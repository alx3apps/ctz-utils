package ru.concerteza.util.db.blob.compress;

import org.iq80.snappy.SnappyInputStream;
import org.iq80.snappy.SnappyOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * BLOB compressor implementation, uses very fast <a href="https://github.com/dain/snappy">Snappy</a> compression method
 *
 * @author alexey
 * Date: 4/14/12
 * @see NoCompressor
 * @see GzipCompressor
 * @see XzCompressor
 * @see ru.concerteza.util.db.blob.tool.BlobTool
 */
public class SnappyCompressor extends AbstractCompressor {
    /**
     * {@inheritDoc}
     */
    protected OutputStream wrapCompressInternal(OutputStream out) throws IOException {
        return new SnappyOutputStream(out);
    }

    /**
     * {@inheritDoc}
     */
    protected InputStream wrapDecompressInternal(InputStream in) throws IOException {
        return new SnappyInputStream(in);
    }
}
