package ru.concerteza.util.db.blob.compress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * User: alexey
 * Date: 4/14/12
 */
public interface Compressor {

    OutputStream wrapCompress(OutputStream out);

    InputStream wrapDecompress(InputStream in);
}
