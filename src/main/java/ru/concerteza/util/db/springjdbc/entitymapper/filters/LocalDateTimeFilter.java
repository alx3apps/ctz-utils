package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import org.joda.time.LocalDateTime;
import ru.concerteza.util.date.CtzDateUtils;

import java.util.Date;


/**
 * Filter implementation to convert {@link java.util.Date} values to <a href="http://joda-time.sourceforge.net/api-release/org/joda/time/LocalDateTime.html">LocalDateTime</a>
 *
 * @author alexey
 * Date: 4/29/12
 * @see ColumnListFilter                         CtzSpri
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 * @see ru.concerteza.util.date.CtzDateUtils#toLocalDateTime(java.util.Date)
 */
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
        return CtzDateUtils.toLocalDateTime((Date) value);
    }
}
