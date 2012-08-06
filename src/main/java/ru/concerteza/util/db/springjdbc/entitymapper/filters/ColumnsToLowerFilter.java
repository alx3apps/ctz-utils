package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import com.google.common.collect.ImmutableMap;
import ru.concerteza.util.db.springjdbc.entitymapper.EntityFilter;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

/**
 * Filter, that switch all column names to lower case, may be useful for <code>select * ...</code> requests for some RDBMS
 *
 * @author alexey
 * Date: 5/16/12
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityFilter
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 */
public class ColumnsToLowerFilter implements EntityFilter {

    /**
     * @param data row data
     * @return same row data wil lower column names
     */
    @Override
    public Map<String, ?> apply(Map<String, ?> data) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        for(Map.Entry<String, ?> en : data.entrySet()) {
            builder.put(en.getKey().toLowerCase(Locale.ENGLISH), en.getValue());
        }
        return builder.build();
    }
}
