package ru.concerteza.util.db.blob.compress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: alexey
 * Date: 4/14/12
 */
public class NoCompressor implements Compressor {
    @Override
    public OutputStream wrapCompress(OutputStream out) {
        return out;
    }

    @Override
    public InputStream wrapDecompress(InputStream in) {
        return in;
    }
}
