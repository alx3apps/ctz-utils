package ru.concerteza.util.db.blob.tool;

import org.apache.commons.dbcp.DelegatingConnection;
import org.postgresql.PGConnection;
import org.postgresql.copy.PGCopyOutputStream;
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
 * User: alexey
 * Date: 2/4/11
 */
public class PostgreBlobTool extends AbstractBlobTool {

    public PostgreBlobTool(DataSource dataSource, Compressor compressor) {
        super(dataSource, compressor);
    }

    @Override
    protected Pair<Long, OutputStream> createInternal() throws SQLException {
        Connection conn = DataSourceUtils.doGetConnection(dataSource);
        LargeObjectManager manager = extractManager(conn);
        long oid = manager.createLO(LargeObjectManager.WRITE);
        LargeObject lob = manager.open(oid, LargeObjectManager.WRITE);
        return new Pair<Long, OutputStream>(oid, lob.getOutputStream());
    }

    @Override
    protected InputStream loadInternal(long oid) throws SQLException {
        Connection conn = DataSourceUtils.doGetConnection(dataSource);
        LargeObjectManager manager = extractManager(conn);
        LargeObject lob = manager.open(oid, LargeObjectManager.READ);
        return lob.getInputStream();
    }

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

