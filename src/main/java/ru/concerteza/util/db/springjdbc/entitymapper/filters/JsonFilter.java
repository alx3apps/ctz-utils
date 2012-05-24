package ru.concerteza.util.db.springjdbc.entitymapper.filters;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Filter implementation to parse JSON data to objects
 *
 * @author : alexey
 * Date: 5/15/12
 * @see ColumnListFilter
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 */
public class JsonFilter extends ColumnListFilter<Object> {
    private final Gson gson;
    private final Map<String, Class<?>> columnMap;

    /**
     * One column constructor
     *
     * @param gson <a href="http://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/Gson.html">Gson</a> instance
     * @param column column name
     * @param clazz class to instantiate from JSON
     */
    public JsonFilter(Gson gson, String column, Class<?> clazz) {
        this(gson, ImmutableMap.<String, Class<?>>of(column, clazz));
    }

    /**
     * Main constructor
     *
     * @param gson <a href="http://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/Gson.html">Gson</a> instance
     * @param columnMap column_name->target_class map
     */
    public JsonFilter(Gson gson, Map<String, Class<?>> columnMap) {
        super(columnMap.keySet());
        this.gson = gson;
        this.columnMap = columnMap;
    }

    /**
     * Method will be called only for columns, provided to constructor
     *
     * @param colname column name
     * @param value in column value
     * @return out column value
     */
    @Override
    protected Object filter(String colname, Object value) {
        Class<?> clazz = columnMap.get(colname);
        checkNotNull(clazz, "JSON class not found for column: '%s', colmap: '%s'", colname, columnMap);
        return gson.fromJson((String) value, clazz);
    }
}
