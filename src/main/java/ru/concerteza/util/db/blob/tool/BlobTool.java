package ru.concerteza.util.db.blob.tool;

import ru.concerteza.util.db.blob.DetachedBlob;
import ru.concerteza.util.db.blob.ReadableBlob;
import ru.concerteza.util.db.blob.WritableBlob;

/**
 * User: alexey
 * Date: 4/13/12
 */

public interface BlobTool {
    // return blob stream to write to, it must be closed by the caller
    WritableBlob create();

    // result stream must be closed by the caller
    ReadableBlob load(long id);

    DetachedBlob detach(long id);

    void delete(long id);
}
