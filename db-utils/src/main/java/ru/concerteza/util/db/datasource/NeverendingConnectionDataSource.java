package ru.concerteza.util.db.datasource;

import org.springframework.jdbc.datasource.SmartDataSource;
import ru.concerteza.util.io.CtzIOUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: alexey
 * Date: 10/21/11
 */
public class NeverendingConnectionDataSource extends AbstractDbcpMimicringDataSource implements SmartDataSource {
    private final ThreadLocal<Connection> conn = new ThreadLocal<Connection>();
    private final Queue<Connection> registry = new ConcurrentLinkedQueue<Connection>();

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

    @Override
    public void close() {
        Connection el;
        while (null != (el = registry.poll())) {
            CtzIOUtils.closeQuietly(el);
        }
    }

    @Override
    public boolean shouldClose(Connection con) {
        return false;
    }
}
