package ru.concerteza.util.db.blob;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: alexey
 * Date: 8/11/11
 */
public class ReadableBlob extends AbstractBlob {
    private final InputStream inputStream;

    public ReadableBlob(long oid, InputStream inputStream) {
        super(oid);
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("id", id).
                append("inputStream", inputStream).
                toString();
    }
}
