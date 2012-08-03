package ru.concerteza.util.db.blob;

import ru.concerteza.util.db.blob.compress.Compressor;

import java.io.*;

/**
 * In-memory compressed representation of BLOB data
 *
 * @author alexey
 * Date: 8/19/11
 * @see AbstractBlob
 * @see ReadableBlob
 * @see WritableBlob
 */
public class DetachedBlob extends AbstractBlob implements Serializable {
    private static final long serialVersionUID = 5752953727569147166L;
    private final byte[] compressedData;
    private final Compressor compressor;

    /**
     * @param id BLOB ID
     * @param compressedData compressedData
     * @param compressor compressor instance
     */
    public DetachedBlob(long id, byte[] compressedData, Compressor compressor) {
        super(id);
        this.compressedData = compressedData;
        this.compressor = compressor;
    }

    /**
     * @return decompressed input stream
     */
    public InputStream getInputStream() {
        InputStream bais = new ByteArrayInputStream(compressedData);
        return compressor.wrapDecompress(bais);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // this method was intentionally left blank
    }
}