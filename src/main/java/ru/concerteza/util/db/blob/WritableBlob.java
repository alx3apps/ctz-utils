package ru.concerteza.util.db.blob;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copyLarge;

/**
 * Output stream connected to compressed BLOB in remote database.
 * Among popular RDBMS only PostgreSQL and Oracle support such behaviour natively.
 * Workarounds (not efficient) may be used to simulate this in other RDBMSes'.
 *
 * @author alexey
 * Date: 8/11/11
 * @see AbstractBlob
 * @see DetachedBlob
 * @see ReadableBlob
 * @see ru.concerteza.util.db.blob.tool.PostgreBlobTool
 * @see ru.concerteza.util.db.blob.tool.ServerSideJdbcBlobTool
 * @see ru.concerteza.util.db.blob.tool.TmpFileJdbcBlobTool
 */
@Deprecated // use com.alexkasko.springjdbc.blob
public class WritableBlob extends AbstractBlob {
    private final OutputStream outputStream;

    /**
     * @param id BLOB ID
     * @param outputStream BLOB output stream
     */
    public WritableBlob(long id, OutputStream outputStream) {
        super(id);
        this.outputStream = outputStream;
    }

    /**
     * @return output stream to write data into BLOB
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Shortcut method, reads provided data into BLOB and closes BLOB
     *
     * @param in stream to read data from
     * @return number ob bytes read
     */
    public long writeAndClose(InputStream in) {
        try {
            return copyLarge(in, outputStream);
        } catch(IOException e) {
            throw new BlobException(e, "Error on writing blob, id: '{}'", id);
        } finally {
            closeQuietly(outputStream);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("id", id).
                append("outputStream", outputStream).
                toString();
    }
}
