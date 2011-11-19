package ru.concerteza.util.db.blob;

import java.io.Closeable;

/**
 * User: alexey
 * Date: 8/11/11
 */
public abstract class AbstractBlob implements Closeable {
    protected final long oid;
    protected final boolean compressed;

    protected AbstractBlob(long oid, boolean compressed) {
        this.oid = oid;
        this.compressed = compressed;
    }

    public long getOid() {
        return oid;
    }

    public boolean isCompressed() {
        return compressed;
    }
}
