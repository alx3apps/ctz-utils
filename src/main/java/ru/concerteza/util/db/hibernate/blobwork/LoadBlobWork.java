package ru.concerteza.util.db.hibernate.blobwork;

import org.hibernate.jdbc.Work;
import ru.concerteza.util.db.blob.CtzPostgreBlobUtils;
import ru.concerteza.util.db.blob.ReadableBlob;
import ru.concerteza.util.value.Holder;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 8/11/11
 */
public class LoadBlobWork implements Work {
    private final Holder<ReadableBlob> holder;
    private final long oid;

    public LoadBlobWork(Holder<ReadableBlob> blobHolder, long oid) {
        this.holder = blobHolder;
        this.oid = oid;
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        ReadableBlob blob = CtzPostgreBlobUtils.loadLob(connection, oid, false);
        holder.set(blob);
    }
}
