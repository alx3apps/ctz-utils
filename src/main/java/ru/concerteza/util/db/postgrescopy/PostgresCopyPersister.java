package ru.concerteza.util.db.postgrescopy;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.concerteza.util.collection.SingleUseIterable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static ru.concerteza.util.io.CtzIOUtils.closeQuietly;

/**
 * Wrapper for postgres binary copy operation.
 *
 * @author alexkasko
 * Date: 5/5/13
 */
public class PostgresCopyPersister {
    private static final Logger logger = LoggerFactory.getLogger(PostgresCopyPersister.class);

    static final byte[] HEADER_BYTES = new byte[]{0x50, 0x47, 0x43, 0x4f, 0x50, 0x59, 0x0a, (byte)0xff, 0x0d, 0x0a,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    static final byte[] EOF_BYTES = new byte[] {(byte) 0xff, (byte) 0xff};

    private final DataSource ds;
    private final PostgresCopyProvider provider;
    private final Iterator<byte[]> data;
    private final byte[] buffer = new byte[1<<17];

    /**
     * Constructor
     *
     * @param dataSource postgres data source
     * @param provider
     * @param data
     */
    public PostgresCopyPersister(DataSource dataSource, PostgresCopyProvider provider, Iterator<byte[]> data) {
        this.ds = dataSource;
        this.provider = provider;
        this.data = data;
    }

    /**
     * Calls postgres binary copy API in separate transaction
     *
     * @param sql copy sql statement
     */
    public void persist(String sql) {
        checkArgument(isNotBlank(sql), "Provided sql is blank");
        logger.debug("Starting 'copy' process for sql: [{}]", sql);
        Connection wrapper = null;
        try {
            wrapper = ds.getConnection();
            PGConnection conn = unwrap(wrapper);
            CopyManager cm = conn.getCopyAPI();
            begin(wrapper);
            copyData(cm, sql);
            commit(wrapper);
            logger.debug("Copy committed");
        } catch (Exception e) {
            rollback(wrapper);
            logger.warn("Copy rolled back");
            throw new PostgresCopyException(e);
        } finally {
            closeQuietly(wrapper);
        }
    }

    private void copyData(CopyManager cm, String sql) throws SQLException {
        CopyIn ci = null;
        try {
            ci = cm.copyIn(sql);
            ci.writeToCopy(HEADER_BYTES, 0, HEADER_BYTES.length);
            long count = 0;
            for (byte[] input : SingleUseIterable.of(data)) {
                int len = provider.fillCopyBuf(input, buffer);
                ci.writeToCopy(buffer, 0, len);
                count += input.length;
            }
            ci.writeToCopy(EOF_BYTES, 0, EOF_BYTES.length);
            long rows = ci.endCopy();
            logger.debug("Copy stream read, bytes: [{}], rows: [{}]", count, rows);
        } catch (Exception e) {
            if (null != ci && ci.isActive()) {
                ci.cancelCopy();
            }
        }
    }

    private PGConnection unwrap(Connection wrapper) throws SQLException {
        if(wrapper instanceof PGConnection) return (PGConnection) wrapper;
        return wrapper.unwrap(PGConnection.class);
    }

    private void begin(Connection con) throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("start transaction");
        } finally {
            closeQuietly(stmt);
        }
    }

    private void commit(Connection con) throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("commit");
        } finally {
            closeQuietly(stmt);
        }
    }

    private void rollback(Connection con) {
        Statement stmt = null;
        try {
            if (null != con) {
                stmt = con.createStatement();
                stmt.executeUpdate("rollback");
            }
        } catch (Exception e1) {
            logger.warn(e1.getMessage(), e1);
        } finally {
            closeQuietly(stmt);
        }
    }
}
