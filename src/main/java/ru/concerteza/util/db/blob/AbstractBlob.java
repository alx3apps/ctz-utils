package ru.concerteza.util.db.blob;

import java.io.Closeable;

/**
 * Abstract class for PostgreSQL like BLOBs
 *
 * @author alexey
 * Date: 8/11/11
 * @see ReadableBlob
 * @see WritableBlob
 * @see DetachedBlob
 */
public abstract class AbstractBlob implements Closeable {
    protected final long id;

    /**
     * @param id blob ID
     */
    protected AbstractBlob(long id) {
        this.id = id;
    }

    /**
     * @return blob ID
     */
    public long getId() {
        return id;
    }
}
