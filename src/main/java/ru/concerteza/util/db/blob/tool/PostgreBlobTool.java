package ru.concerteza.util.db.blob.tool;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import ru.concerteza.util.db.blob.compress.Compressor;
import ru.concerteza.util.value.Pair;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * PostgreSQL specific BLOB tool implementation. Uses Postgre JDBC non-standard LO API.
 *
 * @author alexey
 * Date: 2/4/11
 * @see BlobTool
 * @see ru.concerteza.util.db.blob.ReadableBlob
 * @see ru.concerteza.util.db.blob.WritableBlob
 * @see ru.concerteza.util.db.blob.DetachedBlob
 */
@Deprecated // use com.alexkasko.springjdbc.blob
public class PostgreBlobTool extends AbstractBlobTool {

    public PostgreBlobTool(DataSource dataSource, Compressor compressor) {
        super(dataSource, compressor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Pair<Long, OutputStream> createInternal() throws SQLException {
        Connection conn = DataSourceUtils.doGetConnection(dataSource);
        LargeObjectManager manager = extractManager(conn);
        long oid = manager.createLO(LargeObjectManager.WRITE);
        LargeObject lob = manager.open(oid, LargeObjectManager.WRITE);
        return new Pair<Long, OutputStream>(oid, lob.getOutputStream());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected InputStream loadInternal(long oid) throws SQLException {
        Connection conn = DataSourceUtils.doGetConnection(dataSource);
        LargeObjectManager manager = extractManager(conn);
        LargeObject lob = manager.open(oid, LargeObjectManager.READ);
        return lob.getInputStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteInternal(long oid) throws SQLException {
        Connection conn = DataSourceUtils.doGetConnection(dataSource);
        LargeObjectManager manager = extractManager(conn);
        manager.delete(oid);
    }

    private static LargeObjectManager extractManager(Connection conn) throws SQLException {
        PGConnection pgConn = conn.unwrap(PGConnection.class);
        return pgConn.getLargeObjectAPI();
    }
}

