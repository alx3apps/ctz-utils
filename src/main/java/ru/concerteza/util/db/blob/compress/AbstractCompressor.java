package ru.concerteza.util.db.blob.compress;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: alexey
 * Date: 4/28/12
 */
public abstract class AbstractCompressor implements Compressor {
    @Override
    public OutputStream wrapCompress(OutputStream out) {
        try {
            return wrapCompressInternal(out);
        } catch (Exception e) {
            throw new CompressException("Error on compressing", e);
        }
    }

    @Override
    public InputStream wrapDecompress(InputStream in) {
        try {
            return wrapDecompressInternal(in);
        } catch (Exception e) {
            throw new CompressException("Error on decompressing", e);
        }
    }

    protected abstract OutputStream wrapCompressInternal(OutputStream out) throws Exception;

    protected abstract InputStream wrapDecompressInternal(InputStream in) throws Exception;
}
