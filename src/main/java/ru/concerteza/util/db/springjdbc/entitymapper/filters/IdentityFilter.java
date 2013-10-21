package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import ru.concerteza.util.db.springjdbc.entitymapper.EntityFilter;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Filter implementation that does nothing
 *
 * @author  alexey
 * Date: 6/22/12
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityFilter
 * @deprecated Use {@link ru.concerteza.util.db.springjdbc.entitymapper.EntityFilters#identity()} instead.
 */
@Deprecated
public class IdentityFilter implements EntityFilter {
    public static final IdentityFilter INSTANCE = new IdentityFilter();

    /**
     *
     * @param data row data
     * @return row data untouched
     */
    @Override
    public Map<String, ?> apply(Map<String, ?> data) {
        return data;
    }
}
