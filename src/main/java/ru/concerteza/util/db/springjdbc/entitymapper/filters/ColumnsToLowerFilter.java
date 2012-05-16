package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import com.google.common.collect.ImmutableMap;
import ru.concerteza.util.db.springjdbc.entitymapper.Filter;

import java.util.Map;

/**
 * User: alexey
 * Date: 5/16/12
 */
public class ColumnsToLowerFilter implements Filter {
    @Override
    public Map<String, Object> apply(Map<String, Object> input) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        for(Map.Entry<String, Object> en : input.entrySet()) {
            builder.put(en.getKey().toLowerCase(), en.getValue());
        }
        return builder.build();
    }
}
