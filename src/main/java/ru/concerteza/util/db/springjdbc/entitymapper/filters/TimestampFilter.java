package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import java.sql.Timestamp;
import java.util.Date;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * Filter implementation to parse string representation of dates into {@link java.sql.Timestamp}
 *
 * @author Timofey Gorshkov
 * @author alexey
 * @since  2.3
 * @see ColumnListFilter
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 */
public class TimestampFilter extends ColumnListFilter<Timestamp> {

    /**
     * @param columns columns to apply this filter to
     */
    public TimestampFilter(String... columns) {
        super(columns);
    }

    /**
     * Method will be called only for columns, provided to constructor
     *
     * @param colname column name
     * @param value in column value
     * @return out column value
     */
    @Override
    protected Timestamp filterColumn(String colname, Object value) {
        if (Timestamp.class.isAssignableFrom(value.getClass())) return (Timestamp)value;
        if (Date.class.isAssignableFrom(value.getClass())) return new Timestamp(((Date)value).getTime());
        if (value instanceof String) return Timestamp.valueOf((String)value);
        throw new IllegalArgumentException(format("Illegal argument, column: '{}', class: '{}'", colname, value.getClass().getSimpleName()));
    }
}
