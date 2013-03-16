package ru.concerteza.util.db.blob.compress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * BLOB compressor implementation, uses standard {@code java.util} GZIP implementation
 *
 * @author alexey
 * Date: 4/14/12
 */
@Deprecated // use com.alexkasko.springjdbc.compress
public class GzipCompressor extends AbstractCompressor {
    /**
     * {@inheritDoc}
     */
    @Override
    protected OutputStream wrapCompressInternal(OutputStream out) throws IOException {
        return new GZIPOutputStream(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected InputStream wrapDecompressInternal(InputStream in) throws IOException {
        return new GZIPInputStream(in);
    }
}
