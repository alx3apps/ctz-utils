//package ru.concerteza.util.db.csv;
//
//import com.google.common.collect.BiMap;
//import com.google.common.collect.ImmutableBiMap;
//import ru.concerteza.util.db.jdbcstub.AbstractResultSet;
//import ru.concerteza.util.db.jdbcstub.AbstractResultSetMetadata;
//
//import java.math.BigDecimal;
//import java.sql.*;
//import java.util.Iterator;
//import java.util.Map;
//
//import static com.google.common.base.Preconditions.checkArgument;
//import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;
//
///**
// * User: alexey
// * Date: 6/29/12
// */
//
//@Deprecated
//class MapIteratorResultSet extends AbstractResultSet {
//    private final Iterator<Map<String, String>> data;
//    private BiMap<Integer, String> columnNames;
//    private Map<String, String> current = null;
//
//    public MapIteratorResultSet(Iterator<Map<String, String>> data) {
//        this.data = data;
//    }
//
//    private boolean init() {
//        if(!data.hasNext()) return false;
//        this.current = data.next();
//        ImmutableBiMap.Builder<Integer, String> builder = ImmutableBiMap.builder();
//        int i = 1;
//        for(Map.Entry<String, String> en : current.entrySet()) {
//            builder.put(i, en.getKey());
//            i += 1;
//        }
//        this.columnNames = builder.build();
//        return true;
//    }
//
//    @Override
//    public boolean next() throws SQLException {
//        if(null == current) return init();
//        if(data.hasNext()) {
//            current = data.next();
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public int findColumn(String columnLabel) throws SQLException {
//        return columnNames.inverse().get(columnLabel);
//    }
//
//    @Override
//    public void close() throws SQLException {
//        // read to end to ensure file closing
//        fireTransform(data);
//    }
//
//    @Override
//    public ResultSetMetaData getMetaData() throws SQLException {
//        if (null == current) {
//            boolean initted = init();
//            checkArgument(initted, "Cannot get metadata for empty data");
//        }
//        return new Metadata();
//    }
//
//    // get methods
//
//    @Override
//    public String getString(String columnLabel) throws SQLException {
//        return current.get(columnLabel);
//    }
//
//    @Override
//    public boolean getBoolean(String columnLabel) throws SQLException {
//        return Boolean.parseBoolean(getString(columnLabel));
//    }
//
//    @Override
//    public byte getByte(String columnLabel) throws SQLException {
//        return Byte.parseByte(getString(columnLabel));
//    }
//
//    @Override
//    public short getShort(String columnLabel) throws SQLException {
//        return Short.parseShort(getString(columnLabel));
//    }
//
//    @Override
//    public int getInt(String columnLabel) throws SQLException {
//        return Integer.parseInt(getString(columnLabel));
//    }
//
//    @Override
//    public long getLong(String columnLabel) throws SQLException {
//        return Long.parseLong(getString(columnLabel));
//    }
//
//    @Override
//    public float getFloat(String columnLabel) throws SQLException {
//        return Float.parseFloat(getString(columnLabel));
//    }
//
//    @Override
//    public double getDouble(String columnLabel) throws SQLException {
//        return Double.parseDouble(getString(columnLabel));
//    }
//
//    @Override
//    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
//        return new BigDecimal(getString(columnLabel));
//    }
//
//    @Override
//    public Date getDate(String columnLabel) throws SQLException {
//        return Date.valueOf(getString(columnLabel));
//    }
//
//    @Override
//    public Time getTime(String columnLabel) throws SQLException {
//        return Time.valueOf(getString(columnLabel));
//    }
//
//    @Override
//    public Timestamp getTimestamp(String columnLabel) throws SQLException {
//        return Timestamp.valueOf(getString(columnLabel));
//    }
//
//    @Override
//    public Object getObject(String columnLabel) throws SQLException {
//        return getString(columnLabel);
//    }
//
//    @Override
//    public String getString(int columnIndex) throws SQLException {
//        return getString(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public boolean getBoolean(int columnIndex) throws SQLException {
//        return getBoolean(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public byte getByte(int columnIndex) throws SQLException {
//        return getByte(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public short getShort(int columnIndex) throws SQLException {
//        return getShort(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public int getInt(int columnIndex) throws SQLException {
//        return getInt(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public long getLong(int columnIndex) throws SQLException {
//        return getLong(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public float getFloat(int columnIndex) throws SQLException {
//        return getFloat(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public double getDouble(int columnIndex) throws SQLException {
//        return getDouble(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
//        return getBigDecimal(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public Date getDate(int columnIndex) throws SQLException {
//        return getDate(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public Time getTime(int columnIndex) throws SQLException {
//        return getTime(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public Timestamp getTimestamp(int columnIndex) throws SQLException {
//        return getTimestamp(columnNames.get(columnIndex));
//    }
//
//    @Override
//    public Object getObject(int columnIndex) throws SQLException {
//        return getObject(columnNames.get(columnIndex));
//    }
//
//    private class Metadata extends AbstractResultSetMetadata {
//        @Override
//        public int getColumnCount() throws SQLException {
//            return columnNames.size();
//        }
//
//        @Override
//        public String getColumnName(int column) throws SQLException {
//            return columnNames.get(column);
//        }
//
//        @Override
//        public String getColumnLabel(int column) throws SQLException {
//            return getColumnName(column);
//        }
//
//        @Override
//        public int getColumnType(int column) throws SQLException {
//            return Types.VARCHAR;
//        }
//    }
//}
