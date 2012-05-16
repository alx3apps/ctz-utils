package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.db.springjdbc.entitymapper.Filter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * User: alexey
 * Date: 5/15/12
 */
public abstract class ColumnListFilter<T> implements Filter {
    private final List<String> columns;

    protected ColumnListFilter(String... columns) {
        this.columns = asList(columns);
    }

    protected ColumnListFilter(Collection<String> columns) {
        this.columns = ImmutableList.copyOf(columns);
    }

    @Override
    public Map<String, Object> apply(Map<String, Object> input) {
        ImmutableMap.Builder<String, Object> res = ImmutableMap.builder();
        for (Map.Entry<String, Object> en : input.entrySet()) {
            final Object val;
            if(columns.contains(en.getKey())) {
                val = decorateWrapError(en.getKey(), en.getValue());
            } else {
                val = en.getValue();
            }
            res.put(en.getKey(), val);
        }
        return res.build();
    }

    private Object decorateWrapError(String colname, Object value) {
        try {
            return decorate(colname, value);
        } catch (Exception e) {
            throw new UnhandledException("Error decorating column: " + colname, e);
        }
    }

    protected abstract T decorate(String colname, Object value);
}
