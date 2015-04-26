package ru.concerteza.util.db.datasource;

import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.lang3.builder.*;

/**
 * Abstract class for creating data sources with the same API, as commons-dbcp
 *
 * @author alexey
 * Date: 10/21/11
 */
public abstract class AbstractDbcpMimicringDataSource implements DataSource {
    protected String url;
    protected String username;
    protected String password;

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    //@Override only for java version 1.7, but ctz-utils has code of version 1.6
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    // mimicring BasicDataSource creation interface

    /**
     * @param driverClassName JDBC driver class name
     */
    public void setDriverClassName(String driverClassName) {
        try {
            Class.forName(driverClassName);
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param url JDBC url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @param username user name
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * NO-OP method
     *
     * @param maxActive max active connections
     */
    public void setMaxActive(int maxActive) {
        // this method is intentionally left blank
    }

    /**
     * NO-OP method
     *
     * @param maxIdle max idle connections
     */
    public void setMaxIdle(int maxIdle) {
        // this method is intentionally left blank
    }

    /**
    * NO-OP method
    */
    public void close() {
        // this method is intentionally left blank
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("url", url).
                append("username", username).
                toString();
    }
}
