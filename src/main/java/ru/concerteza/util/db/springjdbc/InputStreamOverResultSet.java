package ru.concerteza.util.db.springjdbc;

import org.springframework.jdbc.InvalidResultSetAccessException;
import ru.concerteza.util.io.ReadableByteArrayOutputStream;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;

import static ru.concerteza.util.db.springjdbc.CtzJdbcUtils.javaClassFromSqlType;

/**
 * {@link InputStream} wrapper for {@link ResultSet}. All column values from all rows are read and serialized
 * using {@link DataOutputStream}. Dates are serialized as strings.
 *
 * @author alexey
 * Date: 8/28/12
 * @see InputStreamOverResultSetTest
 */
public class InputStreamOverResultSet extends InputStream {
    private final ResultSet rs;
    private final ResultSetMetaData rsmd;
    private final ReadableByteArrayOutputStream rbaos;
    private final DataOutputStream dos;
    private final byte[] single = new byte[1];

    public InputStreamOverResultSet(ResultSet rs) {
        try {
            this.rs = rs;
            this.rsmd = rs.getMetaData();
            this.rbaos = new ReadableByteArrayOutputStream();
            this.dos = new DataOutputStream(rbaos);
        } catch(SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }

    public static InputStreamOverResultSet of(ResultSet rs) {
        return new InputStreamOverResultSet(rs);
    }

    @Override
    public int read() throws IOException {
        int wasRead = read(single, 0, 1);
        if(-1 == wasRead) return -1;
        if(0 == wasRead) throw new IllegalStateException(); // cannot happen
        // http://stackoverflow.com/a/842900/314015
        else return single[0] & (0xff);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int wasRead = rbaos.read(b, off, len);
        if(wasRead == len) return wasRead;
        try {
            if(-1 == wasRead) wasRead = 0;
            while (rs.next()) {
                readRow();
                wasRead += rbaos.read(b, off + wasRead, len - wasRead);
                if(wasRead == len) return wasRead;
            }
            return wasRead > 0 ? wasRead : -1;
        } catch(SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }

    private void readRow() {
        try {
            rbaos.reset();
            for(int i = 1; i <= rsmd.getColumnCount(); i++) {
                Class<?> clazz = javaClassFromSqlType(rsmd.getColumnType(i));
                writeFromRs(rs, clazz, i);
                dos.flush();
            }
        } catch(SQLException e) {
            throw new InvalidResultSetAccessException(e);
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void writeFromRs(ResultSet rs, Class<?> clazz, int ind) throws SQLException, IOException {
        if(String.class.equals(clazz)) {
            String data = rs.getString(ind);
            if(null != data) dos.writeUTF(data);
        } else if(boolean.class.equals(clazz)) {
            boolean data = rs.getBoolean(ind);
            dos.writeBoolean(data);
        } else if(byte.class.equals(clazz)) {
            byte data = rs.getByte(ind);
            dos.writeByte(data);
        } else if(short.class.equals(clazz)) {
            short data = rs.getShort(ind);
            dos.writeShort(data);
        } else if(int.class.equals(clazz)) {
            int data = rs.getInt(ind);
            dos.writeInt(data);
        } else if(long.class.equals(clazz)) {
            long data = rs.getLong(ind);
            dos.writeLong(data);
        } else if(float.class.equals(clazz)) {
            float data = rs.getFloat(ind);
            dos.writeFloat(data);
        } else if(double.class.equals(clazz)) {
            double data = rs.getDouble(ind);
            dos.writeDouble(data);
        } else if(BigDecimal.class.equals(clazz)) {
            BigDecimal data = rs.getBigDecimal(ind);
            double primitive = null != data ? data.doubleValue() : 0;
            dos.writeDouble(primitive);
        } else if(byte[].class.equals(clazz)) {
            byte[] data = rs.getBytes(ind);
            if(null != data) dos.write(data);
        } else if(java.sql.Date.class.equals(clazz)) {
            java.sql.Date data = rs.getDate(ind);
            if(null != data) dos.writeUTF(data.toString());
        } else if(Time.class.equals(clazz)) {
            Time data = rs.getTime(ind);
            if(null != data) dos.writeUTF(data.toString());
        } else if(Timestamp.class.equals(clazz)) {
            Timestamp data = rs.getTimestamp(ind);
            if(null != data) dos.writeUTF(data.toString());
        } else {
            Object data = rs.getObject(ind);
            if(null != data) dos.writeUTF(data.toString());
        }
    }

    @Override
    public void close() throws IOException {
        dos.close();
    }
}