package ru.concerteza.util.db.csv;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.LocalDateTime;
import ru.concerteza.util.db.jdbcstub.AbstractResultSet;
import ru.concerteza.util.db.jdbcstub.AbstractResultSetMetadata;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import static com.google.common.base.Preconditions.checkArgument;
import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;

/**
 * {@link ResultSet} implementation over provided map data iterator
 *
 * @author alexey
 * Date: 6/29/12
 */
class MapIteratorResultSet extends AbstractResultSet {
    private final Iterator<Map<String, String>> data;
    private BiMap<Integer, String> columnNames;
    private Map<String, String> current = null;
    private boolean metadataInitFired = false;

    /**
     * @param data iterator over CSV data
     */
    public MapIteratorResultSet(Iterator<Map<String, String>> data) {
        this.data = data;
    }

    private boolean init() {
        if(!data.hasNext()) return false;
        this.current = data.next();
        ImmutableBiMap.Builder<Integer, String> builder = ImmutableBiMap.builder();
        int i = 1;
        for(Map.Entry<String, String> en : current.entrySet()) {
            builder.put(i, en.getKey());
            i += 1;
        }
        this.columnNames = builder.build();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean next() throws SQLException {
        if(null == current) return init();
        if(metadataInitFired) {
            metadataInitFired = false;
            return true;
        }
        if(data.hasNext()) {
            current = data.next();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return columnNames.inverse().get(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SQLException {
        // read to end to ensure file closing
        fireTransform(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        if (null == current) {
            boolean initted = init();
            checkArgument(initted, "Cannot get metadata for empty data");
            metadataInitFired = true;
        }
        return new Metadata();
    }

    // get methods

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(String columnLabel) throws SQLException {
        return current.get(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return Boolean.parseBoolean(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return Byte.parseByte(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(String columnLabel) throws SQLException {
        return Short.parseShort(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(String columnLabel) throws SQLException {
        return Integer.parseInt(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(String columnLabel) throws SQLException {
        return Long.parseLong(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return Float.parseFloat(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return Double.parseDouble(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        try {
            String s = getString(columnLabel);
            s = StringUtils.deleteWhitespace(s);
            return Hex.decodeHex(s.toCharArray());
        } catch (DecoderException ex) {
            throw new SQLException("Unable to decode hex string in column " + columnLabel, ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return new BigDecimal(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return Date.valueOf(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return Time.valueOf(getString(columnLabel));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        String str = getString(columnLabel);
        return StringUtils.isBlank(str) ? null : Timestamp.valueOf(str);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getString(columnLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString(int columnIndex) throws SQLException {
        return getString(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return getBoolean(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return getByte(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getShort(int columnIndex) throws SQLException {
        return getShort(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInt(int columnIndex) throws SQLException {
        return getInt(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLong(int columnIndex) throws SQLException {
        return getLong(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return getFloat(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return getDouble(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return getBigDecimal(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return getDate(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return getTime(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getTimestamp(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return getObject(columnNames.get(columnIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return getTimestamp(columnNames.get(columnIndex), cal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {

        Timestamp ts = getTimestamp(columnLabel);
        if (null == ts) return null;

        LocalDateTime ldt = new LocalDateTime(ts.getTime());
        cal = (cal == null) ? new GregorianCalendar() : (Calendar)cal.clone();
        cal.set(Calendar.YEAR, ldt.getYear());
        cal.set(Calendar.MONTH, ldt.getMonthOfYear() - 1);
        cal.set(Calendar.DAY_OF_MONTH, ldt.getDayOfMonth());
        cal.set(Calendar.HOUR_OF_DAY, ldt.getHourOfDay());
        cal.set(Calendar.MINUTE, ldt.getMinuteOfHour());
        cal.set(Calendar.SECOND, ldt.getSecondOfMinute());
        cal.set(Calendar.MILLISECOND, 0);

        Timestamp result = new Timestamp(cal.getTime().getTime());
        result.setNanos(ts.getNanos());
        return result;
    }

    private class Metadata extends AbstractResultSetMetadata {
        @Override
        public int getColumnCount() throws SQLException {
            return columnNames.size();
        }

        @Override
        public String getColumnName(int column) throws SQLException {
            return columnNames.get(column);
        }

        @Override
        public String getColumnLabel(int column) throws SQLException {
            return getColumnName(column);
        }

        @Override
        public int getColumnType(int column) throws SQLException {
            return Types.VARCHAR;
        }
    }
}
