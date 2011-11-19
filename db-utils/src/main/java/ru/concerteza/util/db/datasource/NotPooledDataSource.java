package ru.concerteza.util.db.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: Oct 27, 2010
 */
public class NotPooledDataSource extends AbstractDbcpMimicringDataSource {

    @Override
    public synchronized Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}