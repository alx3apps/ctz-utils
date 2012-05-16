package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import java.sql.Timestamp;
import java.util.Date;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * @author Timofey Gorshkov
 * @since  2.3
 */
public class TimestampFilter extends ColumnListFilter<Timestamp> {

    public TimestampFilter(String... columns) {
        super(columns);
    }

    @Override
    protected Timestamp decorate(String colname, Object value) {
        if (Timestamp.class.isAssignableFrom(value.getClass())) return (Timestamp)value;
        if (Date.class.isAssignableFrom(value.getClass())) return new Timestamp(((Date)value).getTime());
        if (value instanceof String) return Timestamp.valueOf((String)value);
        throw new IllegalArgumentException(format("Illegal argument, column: '{}', class: '{}'", colname, value.getClass().getSimpleName()));
    }
}
