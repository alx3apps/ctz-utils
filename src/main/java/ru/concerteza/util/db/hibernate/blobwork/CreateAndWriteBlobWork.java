package ru.concerteza.util.db.hibernate.blobwork;

import org.apache.commons.io.IOUtils;
import org.hibernate.jdbc.Work;
import ru.concerteza.util.db.blob.CtzPostgreBlobUtils;
import ru.concerteza.util.db.blob.WriteableBlob;
import ru.concerteza.util.value.Holder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 8/19/11
 */
public class CreateAndWriteBlobWork implements Work {
    private final Holder<Long> oidHolder;
    private final InputStream inputStream;

    public CreateAndWriteBlobWork(Holder<Long> oidHolder, InputStream inputStream) {
        this.oidHolder = oidHolder;
        this.inputStream = inputStream;
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        WriteableBlob blob = null;
        try {
            blob = CtzPostgreBlobUtils.createLob(connection, true);
            IOUtils.copyLarge(inputStream, blob.getOutputStream());
            oidHolder.set(blob.getOid());
        } catch (IOException e) {
            throw new SQLException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(blob);
        }
    }
}
