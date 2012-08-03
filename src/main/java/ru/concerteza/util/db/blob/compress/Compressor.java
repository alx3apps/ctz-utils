package ru.concerteza.util.db.blob.compress;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for different BLOB compression methods through {@link ru.concerteza.util.db.blob.tool.BlobTool} facade
 *
 * @author  alexey
 * Date: 4/14/12
 * @see ru.concerteza.util.db.blob.tool.BlobTool
 */
public interface Compressor {

    /**
     * Must compress provided stream
     *
     * @param out stream to compress
     * @return compressed stream
     */
    OutputStream wrapCompress(OutputStream out);

    /**
     * Must decompress provided stream
     *
     * @param in stream to decompress
     * @return decompressed stream
     */
    InputStream wrapDecompress(InputStream in);
}
