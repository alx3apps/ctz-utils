package ru.concerteza.util.db.hibernate.blobwork;

import org.hibernate.jdbc.Work;
import ru.concerteza.util.db.blob.PostgreBlobUtils;
import ru.concerteza.util.db.blob.WriteableBlob;
import ru.concerteza.util.value.Holder;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 8/11/11
 */
public class CreateBlobWork implements Work {
    private final Holder<WriteableBlob> holder;

    public CreateBlobWork(Holder<WriteableBlob> outputStreamHolder) {
        this.holder = outputStreamHolder;
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        WriteableBlob blob = PostgreBlobUtils.createLob(connection, true);
        holder.set(blob);
    }
}
