package ru.concerteza.util.db.datasource;

import org.springframework.jdbc.datasource.SmartDataSource;
import ru.concerteza.util.io.CtzIOUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * {@link javax.sql.DataSource} implementation that never closes its connections.
 * Connections are cached per thread.
 *
 * @author alexey
 * Date: 10/21/11
 */
public class NeverendingConnectionDataSource extends AbstractDbcpMimicringDataSource implements SmartDataSource {
    private final ThreadLocal<Connection> conn = new ThreadLocal<Connection>();
    private final Queue<Connection> registry = new ConcurrentLinkedQueue<Connection>();

    /**
     * @return thread local cached connection
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection cached = conn.get();
        final Connection res;
        if(null == cached || cached.isClosed()) {
            res = DriverManager.getConnection(url, username, password);
            conn.set(res);
            registry.add(res);
        } else {
            res = cached;
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        Connection el;
        while (null != (el = registry.poll())) {
            CtzIOUtils.closeQuietly(el);
        }
    }

    /**
     * @param con connection
     * @return false
     */
    @Override
    public boolean shouldClose(Connection con) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger");
    }
}
