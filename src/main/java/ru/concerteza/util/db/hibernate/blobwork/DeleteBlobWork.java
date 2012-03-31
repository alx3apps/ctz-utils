package ru.concerteza.util.db.hibernate.blobwork;

import org.hibernate.jdbc.Work;
import ru.concerteza.util.db.blob.CtzPostgreBlobUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 8/12/11
 */
public class DeleteBlobWork implements Work {
    private final long oid;

    public DeleteBlobWork(long oid) {
        this.oid = oid;
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        CtzPostgreBlobUtils.deleteLob(connection, oid);
    }
}
