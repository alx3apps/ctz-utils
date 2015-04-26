package ru.concerteza.util.db.blob;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copyLarge;

/**
 * Input stream connected to persistent compressed BLOB in remote database
 *
 * @author alexey
 * Date: 8/11/11
 * @see AbstractBlob
 * @see DetachedBlob
 * @see WritableBlob
 */
@Deprecated // use com.alexkasko.springjdbc.blob
public class ReadableBlob extends AbstractBlob {
    private final InputStream inputStream;

    /**
     * @param id BLOB ID
     * @param inputStream decompressed blob data stream
     */
    public ReadableBlob(long id, InputStream inputStream) {
        super(id);
        this.inputStream = inputStream;
    }

    /**
     * @return decompressed blob data stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Shortcut method, reads all BLOB data into provided stream and close BLOB
     *
     * @param out stream to write data into
     * @return number ob bytes read
     */
    public long readAndClose(OutputStream out) {
        try {
            return copyLarge(inputStream, out);
        } catch(IOException e) {
            throw new BlobException(e, "Error on reading blob, id: '{}'", id);
        } finally {
            closeQuietly(inputStream);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("id", id).
                append("inputStream", inputStream).
                toString();
    }
}
