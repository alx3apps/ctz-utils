package ru.concerteza.util.db.postgrescopy;

import com.google.common.io.CountingInputStream;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.concerteza.util.io.CtzIOUtils.closeQuietly;

/**
 * Wrapper for postgres binary copy operation.
 *
 * @author alexkasko
 * Date: 5/5/13
 */
public class PostgresCopyPersister {
    private static final Logger logger = LoggerFactory.getLogger(PostgresCopyPersister.class);

    private final DataSource ds;

    /**
     * Constructor
     *
     * @param dataSource postgres data source
     */
    public PostgresCopyPersister(DataSource dataSource) {
        this.ds = dataSource;
    }

    /**
     * Calls postgres binary copy API in separate transaction
     *
     * @param sql copy sql statement
     * @param stream postgres copy stream
     */
    public void persist(String sql, InputStream stream) {
        checkArgument(isNotBlank(sql), "Provided sql is blank");
        checkNotNull(stream, "Provided copy stream is null");
        logger.debug("Starting 'copy' process for sql: [{}]", sql);
        Connection wrapper = null;
        try {
            wrapper = ds.getConnection();
            PGConnection conn = unwrap(wrapper);
            CopyManager cm = conn.getCopyAPI();
            CountingInputStream cis = new CountingInputStream(stream);
            begin(wrapper);
            long rows = cm.copyIn(sql, cis);
            logger.debug("Copy stream read, bytes: [{}], rows: [{}]", cis.getCount(), rows);
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
