package ru.concerteza.util.db.blob.compress;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Basic compressor implementation
 *
 * @author alexey
 * Date: 4/28/12
 */
@Deprecated // use com.alexkasko.springjdbc.compress
public abstract class AbstractCompressor implements Compressor {
    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream wrapCompress(OutputStream out) {
        try {
            return wrapCompressInternal(out);
        } catch (Exception e) {
            throw new CompressException("Error on compressing", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream wrapDecompress(InputStream in) {
        try {
            return wrapDecompressInternal(in);
        } catch (Exception e) {
            throw new CompressException("Error on decompressing", e);
        }
    }

    /**
     * Must compress provided stream
     *
     * @param out stream to compress
     * @return compressed stream
     * @throws Exception
     */
    protected abstract OutputStream wrapCompressInternal(OutputStream out) throws Exception;

    /**
     * Must decompress provided stream
     *
     * @param in stream to decompress
     * @return decompressed stream
     * @throws Exception
     */
    protected abstract InputStream wrapDecompressInternal(InputStream in) throws Exception;
}
