package ru.concerteza.util.db.blob;

import java.io.Closeable;

/**
 * User: alexey
 * Date: 8/11/11
 */
public abstract class AbstractBlob implements Closeable {
    protected final long id;

    protected AbstractBlob(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
