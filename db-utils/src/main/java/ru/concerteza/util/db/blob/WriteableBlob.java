package ru.concerteza.util.db.blob;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.IOException;
import java.io.OutputStream;

/**
 * User: alexey
 * Date: 8/11/11
 */
public class WriteableBlob extends AbstractBlob {
    private final OutputStream outputStream;

    WriteableBlob(long oid, boolean compressed, OutputStream outputStream) {
        super(oid, compressed);
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("oid", oid).
                append("outputStream", outputStream).
                toString();
    }
}
