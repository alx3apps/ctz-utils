package ru.concerteza.util.db.jdbcstub;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * Prepared statement stub, implements all void methods as no-ops.
 *
 * @author alexey
 * Date: 6/29/12
 */
public abstract class NoOpPreparedStatement extends AbstractPreparedStatement {
    /**
     * {@inheritDoc}
     */
    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearParameters() throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBatch() throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxRows(int max) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel() throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearWarnings() throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCursorName(String name) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFetchDirection(int direction) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFetchSize(int rows) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBatch(String sql) throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearBatch() throws SQLException {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        // noop
    }
}
