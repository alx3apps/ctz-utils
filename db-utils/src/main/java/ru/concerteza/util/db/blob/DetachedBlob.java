package ru.concerteza.util.db.blob;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;

/**
 * User: alexey
 * Date: 8/19/11
 */
public class DetachedBlob extends AbstractBlob implements Serializable {
    private static final long serialVersionUID = 5752953727569147166L;

    private final byte[] data;

    public DetachedBlob(long oid, boolean compressed, byte[] data) {
        super(oid, compressed);
        this.data = data;
    }

    public byte[] getData() {
        try {
            final byte[] res;
            if (compressed) {
                ByteArrayInputStream baos = new ByteArrayInputStream(data);
                GZIPInputStream gzipStream = new GZIPInputStream(baos);
                res = IOUtils.toByteArray(gzipStream);
            } else {
                res = data;
            }
            return res;
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    @Override
    public void close() throws IOException {
        // this method is intentionally left blank
    }
}