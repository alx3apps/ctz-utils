package ru.concerteza.util.db.blob.compress;

import org.iq80.snappy.SnappyInputStream;
import org.iq80.snappy.SnappyOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: alexey
 * Date: 4/14/12
 */
public class SnappyCompressor extends AbstractCompressor {
    @Override
    protected OutputStream wrapCompressInternal(OutputStream out) throws IOException {
        return new SnappyOutputStream(out);
    }

    @Override
    protected InputStream wrapDecompressInternal(InputStream in) throws IOException {
        return new SnappyInputStream(in);
    }
}
