package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import ru.concerteza.util.db.springjdbc.entitymapper.ColumnListFilter;

import java.time.LocalDateTime;
import java.util.Date;

import static ru.concerteza.util.date.CtzDateUtils.toLocalDateTime;


/**
 * Filter implementation to convert {@link java.util.Date} values to {@link LocalDateTime}.
 *
 * @author alexey
 * Date: 4/29/12
 * @see ColumnListFilter                         CtzSpri
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 * @see ru.concerteza.util.date.CtzDateUtils#toLocalDateTime(java.util.Date)
 * @deprecated Use {@link ru.concerteza.util.db.springjdbc.entitymapper.EntityFilters#toLocalDateTime} instead.
 */
@Deprecated
public class LocalDateTimeFilter extends ColumnListFilter<LocalDateTime> {
    /**
     * @param columns columns to apply this filter to
     */
    public LocalDateTimeFilter(String... columns) {
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
    protected LocalDateTime filterColumn(String colname, Object value) {
        return toLocalDateTime((Date) value);
    }
}
