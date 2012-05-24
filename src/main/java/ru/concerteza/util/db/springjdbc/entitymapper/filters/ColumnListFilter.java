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
 * Abstract {@link Filter} implementation that will be applied only to given columns
 *
 * @author alexey
 * Date: 5/15/12
 * @see ru.concerteza.util.db.springjdbc.entitymapper.Filter
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 */
public abstract class ColumnListFilter<T> implements Filter {
    private final List<String> columns;

    /**
     * Vararg constructor
     * @param columns columns to apply this filter to
     */
    protected ColumnListFilter(String... columns) {
        this.columns = asList(columns);
    }

    /**
     * List constructor
     * @param columns columns to apply this filter to
     */
    protected ColumnListFilter(Collection<String> columns) {
        this.columns = ImmutableList.copyOf(columns);
    }

    /**
     * @param input in row data
     * @return out row data
     */
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
            return filter(colname, value);
        } catch (Exception e) {
            throw new UnhandledException("Error decorating column: " + colname, e);
        }
    }

    /**
     * Method will be called only for columns, provided to constructor
     *
     * @param colname column name
     * @param value in column value
     * @return out column value
     */
    protected abstract T filter(String colname, Object value);
}
