package ru.concerteza.util.db.jdbcstub;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * User: alexey
 * Date: 6/29/12
 */
public abstract class NoOpPreparedStatement extends AbstractPreparedStatement {
    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        // noop
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        // noop
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        // noop
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        // noop
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        // noop
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        // noop
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        // noop
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        // noop
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        // noop
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        // noop
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        // noop
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        // noop
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        // noop
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        // noop
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // noop
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // noop
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // noop
    }

    @Override
    public void clearParameters() throws SQLException {
        // noop
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        // noop
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        // noop
    }

    @Override
    public void addBatch() throws SQLException {
        // noop
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        // noop
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        // noop
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        // noop
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        // noop
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        // noop
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        // noop
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        // noop
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        // noop
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        // noop
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        // noop
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        // noop
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        // noop
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        // noop
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        // noop
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        // noop
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        // noop
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        // noop
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        // noop
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        // noop
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        // noop
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        // noop
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        // noop
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        // noop
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        // noop
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        // noop
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        // noop
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        // noop
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        // noop
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        // noop
    }

    @Override
    public void close() throws SQLException {
        // noop
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        // noop
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        // noop
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        // noop
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        // noop
    }

    @Override
    public void cancel() throws SQLException {
        // noop
    }

    @Override
    public void clearWarnings() throws SQLException {
        // noop
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        // noop
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        // noop
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        // noop
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        // noop
    }

    @Override
    public void clearBatch() throws SQLException {
        // noop
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        // noop
    }
}
