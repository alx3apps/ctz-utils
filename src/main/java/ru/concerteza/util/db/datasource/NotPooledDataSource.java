package ru.concerteza.util.db.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * {@link javax.sql.DataSource} implementation that doesn't use any connection pool
 *
 * @author alexey
 * Date: Oct 27, 2010
 */
public class NotPooledDataSource extends AbstractDbcpMimicringDataSource {

    /**
     * @return new connection to database without any caching or pools
     * @throws SQLException
     */
    @Override
    public synchronized Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * {@inheritDoc}
     */
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger");
    }
}