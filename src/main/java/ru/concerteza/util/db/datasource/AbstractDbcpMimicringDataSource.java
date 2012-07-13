package ru.concerteza.util.db.datasource;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 10/21/11
 */

// abstract class for creating data sources with the same spring config, as commons-dbcp ones
public abstract class AbstractDbcpMimicringDataSource implements DataSource {
    protected String url;
    protected String username;
    protected String password;

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new NotImplementedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new NotImplementedException();
    }

    // mimicring BasicDataSource creation interface
    public void setDriverClassName(String driverClassName) {
        try {
            Class.forName(driverClassName);
        } catch(ClassNotFoundException e) {
            throw new UnhandledException(e);
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMaxActive(int maxActive) {
        // this method is intentionally left blank
    }

    public void setMaxIdle(int maxIdle) {
        // this method is intentionally left blank
    }

    public void close() {
        // this method is intentionally left blank
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("url", url).
                append("username", username).
                toString();
    }
}
