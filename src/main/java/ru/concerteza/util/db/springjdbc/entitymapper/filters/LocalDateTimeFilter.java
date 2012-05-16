package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import org.joda.time.LocalDateTime;
import ru.concerteza.util.date.CtzDateUtils;

import java.util.Date;


/**
 * User: alexey
 * Date: 4/29/12
 */
public class LocalDateTimeFilter extends ColumnListFilter<LocalDateTime> {
    public LocalDateTimeFilter(String... columns) {
        super(columns);
    }

    @Override
    protected LocalDateTime decorate(String colname, Object value) {
        return CtzDateUtils.toLocalDateTime((Date) value);
    }
}
