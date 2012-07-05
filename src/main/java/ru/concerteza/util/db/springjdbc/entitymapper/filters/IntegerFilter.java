package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import java.util.Collection;
import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * {@link ru.concerteza.util.db.springjdbc.entitymapper.EntityFilter} implementation to convert integer columns that may be
 * represented as {@link String} to {@link Integer}.
 *
 * Initially it was created for cases where {@link java.sql.ResultSet} may be provided by
 * {@link ru.concerteza.util.db.ResultSetOverCSV}.
 *
 * @author Timofey Gorshkov
 * created 29.05.2012
 * @since  2.5.1
 * @see ColumnListFilter
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 * @see ru.concerteza.util.db.ResultSetOverCSV
 */
public class IntegerFilter extends ColumnListFilter<Integer> {

    /**
     * @param columns columns to apply this filter to
     */
    public IntegerFilter(String... columns) {
        super(columns);
    }

    /**
     * @param columns columns to apply this filter to
     */
    public IntegerFilter(Collection<String> columns) {
        super(columns);
    }

    /**
     * Method will be called only for columns, provided to constructor
     *
     * @param colname column name
     * @param value input column value
     * @return output column value
     */
    @Override
    protected Integer filterColumn(String colname, Object value) {
        if (value instanceof Integer) return (Integer)value;
        if (value instanceof String) return Integer.parseInt((String)value);
        throw new IllegalArgumentException(format("Illegal argument, column: '{}', class: '{}'", colname, value.getClass().getSimpleName()));
    }
}
