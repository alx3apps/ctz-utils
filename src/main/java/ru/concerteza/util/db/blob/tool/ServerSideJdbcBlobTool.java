package ru.concerteza.util.db.blob.tool;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.datasource.DataSourceUtils;
import ru.concerteza.util.db.blob.compress.Compressor;
import ru.concerteza.util.value.Pair;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 4/27/12
 */
public class ServerSideJdbcBlobTool extends AbstractJdbcBlobTool {
    public ServerSideJdbcBlobTool(DataSource dataSource, Compressor compressor) {
        super(dataSource, compressor);
    }

    public ServerSideJdbcBlobTool(DataSource dataSource, Compressor compressor, String generateIdSQL, String insertSQL, String loadSQL, String deleteSQL) {
        super(dataSource, compressor, generateIdSQL, insertSQL, loadSQL, deleteSQL);
    }

    @Override
    protected Pair<Long, OutputStream> createInternal() throws SQLException {
        long id = jt.getJdbcOperations().queryForLong(generateIdSQL);
        Connection conn = DataSourceUtils.doGetConnection(dataSource);
        Blob blob = conn.createBlob();
        jt.update(insertSQL, ImmutableMap.of("id", id, "data", blob));
        Blob created = jt.queryForObject(loadSQL, ImmutableMap.of("id", id), Blob.class);
        OutputStream blobStream = created.setBinaryStream(1);
        return new Pair<Long, OutputStream>(id, blobStream);
    }
}
