package ru.concerteza.util.db.blob.tool;

import ru.concerteza.util.db.blob.DetachedBlob;
import ru.concerteza.util.db.blob.ReadableBlob;
import ru.concerteza.util.db.blob.WritableBlob;

/**
 * Front-end for BLOB operations.
 *
 * @author alexey
 * Date: 4/13/12
 * @see ReadableBlob
 * @see WritableBlob
 * @see DetachedBlob
 */
public interface BlobTool {
    /**
     * @return BLOB stream to write to, it must be closed by the caller
     */
    WritableBlob create();

    // result

    /**
     * @param id BLOB ID
     * @return BLOB data stream, must be closed by the caller
     */
    ReadableBlob load(long id);

    /**
     * @param id BLOB ID
     * @return BLOB data in detached (in-memory) mode
     */
    DetachedBlob detach(long id);

    /**
     * @param id BLOB ID
     */
    void delete(long id);
}
