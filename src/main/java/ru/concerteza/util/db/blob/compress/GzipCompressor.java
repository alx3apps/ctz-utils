package ru.concerteza.util.db.blob.compress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * User: alexey
 * Date: 4/14/12
 */
public class GzipCompressor implements Compressor {
    @Override
    public OutputStream wrapCompress(OutputStream out) throws IOException {
        return new GZIPOutputStream(out);
    }

    @Override
    public InputStream wrapDecompress(InputStream in) throws IOException {
        return new GZIPInputStream(in);
    }
}
