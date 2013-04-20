package ru.concerteza.util.net.sftp;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * User: alexkasko
 * Date: 4/19/13
 */
public class SftpFile implements Closeable {
    private final InputStream stream;
    private final String name;

    SftpFile(String name, InputStream stream) {
        checkNotNull(stream, "Provided stream is null");
        checkArgument(isNotBlank(name), "Provided name is blank");
        this.stream = stream;
        this.name = name;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    public InputStream getStream() {
        return stream;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).
                append("stream", stream).
                append("name", name).
                toString();
    }
}

