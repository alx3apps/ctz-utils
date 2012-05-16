package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 5/15/12
 */
public class JsonFilter extends ColumnListFilter<Object> {
    private final Gson gson;
    private final Map<String, Class<?>> columnMap;

    public JsonFilter(Gson gson, String column, Class<?> clazz) {
        this(gson, ImmutableMap.<String, Class<?>>of(column, clazz));
    }

    public JsonFilter(Gson gson, Map<String, Class<?>> columnMap) {
        super(columnMap.keySet());
        this.gson = gson;
        this.columnMap = columnMap;
    }

    @Override
    protected Object decorate(String colname, Object value) {
        Class<?> clazz = columnMap.get(colname);
        checkNotNull(clazz, "JSON class not found for column: '%s', colmap: '%s'", colname, columnMap);
        return gson.fromJson((String) value, clazz);
    }
}
