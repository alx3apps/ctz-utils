package ru.concerteza.util.db.hibernate.blobwork;

import org.apache.commons.io.IOUtils;
import org.hibernate.jdbc.Work;
import ru.concerteza.util.db.blob.DetachedBlob;
import ru.concerteza.util.db.blob.PostgreBlobUtils;
import ru.concerteza.util.db.blob.ReadableBlob;
import ru.concerteza.util.value.Holder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 8/19/11
 */
public class LoadDetachedBlobWork implements Work {
    private final Holder<DetachedBlob> holder;
    private final long oid;

    public LoadDetachedBlobWork(Holder<DetachedBlob> blobHolder, long oid) {
        this.holder = blobHolder;
        this.oid = oid;
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        ReadableBlob readable = null;
        try {
            readable = PostgreBlobUtils.loadLob(connection, oid, false);
            byte[] data = IOUtils.toByteArray(readable.getInputStream());
            DetachedBlob res = new DetachedBlob(readable.getOid(), readable.isCompressed(), data);
            holder.set(res);
        } catch (IOException e) {
            throw new SQLException(e);
        } finally {
            IOUtils.closeQuietly(readable);
        }
    }
}
