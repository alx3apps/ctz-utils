package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import com.google.common.collect.ImmutableMap;
import ru.concerteza.util.db.springjdbc.entitymapper.Filter;

import java.util.Map;

/**
 * Filter, that switch all column names to lower case, may be useful for <code>select * ...</code> requests for some RDBMS
 *
 * @author alexey
 * Date: 5/16/12
 * @see ru.concerteza.util.db.springjdbc.entitymapper.Filter
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 */
public class ColumnsToLowerFilter implements Filter {
    /**
     * @param input in row data
     * @return out row data
     */
    @Override
    public Map<String, Object> apply(Map<String, Object> input) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        for(Map.Entry<String, Object> en : input.entrySet()) {
            builder.put(en.getKey().toLowerCase(), en.getValue());
        }
        return builder.build();
    }
}
