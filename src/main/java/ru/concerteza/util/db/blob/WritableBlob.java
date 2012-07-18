package ru.concerteza.util.db.blob;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copyLarge;

/**
 * User: alexey
 * Date: 8/11/11
 */
public class WritableBlob extends AbstractBlob {
    private final OutputStream outputStream;

    public WritableBlob(long oid, OutputStream outputStream) {
        super(oid);
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public long writeAndClose(InputStream in) {
        try {
            return copyLarge(in, outputStream);
        } catch(IOException e) {
            throw new BlobException(e, "Error on writing blob, id: '{}'", id);
        } finally {
            closeQuietly(outputStream);
        }
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("id", id).
                append("outputStream", outputStream).
                toString();
    }
}
