package ru.concerteza.util.db.springjdbc;

import com.google.common.collect.AbstractIterator;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Iterable over single result set row. Column name and value retrieval borrowed
 * from spring's {@code ColumnMapRowMapper} Will NOT call {@link java.sql.ResultSet#next()}
 *
 * @author alexey
 *         Date: 8/6/12
 */
public class RowIterable implements Iterable<RowIterable.Cell> {
    private final ResultSet rs;
    private final ResultSetMetaData rsmd;
    private final int columnCount;

    /**
     * @param rs result set to iterate over its single row
     */
    public RowIterable(ResultSet rs) {
        try {
            this.rs = rs;
            this.rsmd = rs.getMetaData();
            this.columnCount = rsmd.getColumnCount();
        } catch(SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }

    /**
     * Factory method
     *
     * @param rs result set to iterate over its single row
     * @return row iterable instance
     */
    public static RowIterable of(ResultSet rs) {
        return new RowIterable(rs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Cell> iterator() {
        return new RowIterator();
    }

    /**
     * Iterator over single result set row
     */
    public class RowIterator extends AbstractIterator<RowIterable.Cell> {
        private int index = 1;

        /**
         * {@inheritDoc}
         */
        @Override
        protected Cell computeNext() {
            if(index > columnCount) return endOfData();
            try {
                String colname = JdbcUtils.lookupColumnName(rsmd, index);
                Object val = JdbcUtils.getResultSetValue(rs, index);
                index += 1;
                return new Cell(colname, val);
            } catch(SQLException e) {
                throw new InvalidResultSetAccessException(e);
            }
        }
    }

    /**
     * Result set data cell
     */
    public static class Cell {
        private final String columnName;
        private final Object value;

        /**
         * @param columnName column name
         * @param value      column value
         */
        public Cell(String columnName, Object value) {
            checkNotNull(columnName, "Provided column name is null");
            this.columnName = columnName;
            this.value = value;
        }

        /**
         * @return column name
         */
        public String getColumnName() {
            return columnName;
        }

        /**
         * @return data cell value
         */
        public Object getValue() {
            return value;
        }
    }
}
