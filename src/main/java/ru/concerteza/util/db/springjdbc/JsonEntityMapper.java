package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Function;
import com.google.gson.Gson;
import org.apache.commons.lang.UnhandledException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static ru.concerteza.util.CtzFormatUtils.format;
import static ru.concerteza.util.CtzReflectionUtils.callDefaultConstructor;

/**
 * User: alexey
 * Date: 4/14/12
 */
public class JsonEntityMapper<T> implements RowMapper<T> {
    private final ColumnMapRowMapper mapper = new ColumnMapRowMapper();
    private final Class<T> clazz;
    private final Map<String, Field> columnMap;
    private final Gson gson;
    private final List<Filter> filters = new ArrayList<Filter>();

    public JsonEntityMapper(Class<T> clazz, Map<String, Field> fieldMap, Filter... filters) {
        this(clazz, fieldMap, null, filters);
    }

    public JsonEntityMapper(Class<T> clazz, Map<String, Field> fieldMap, Gson gson, Filter... filters) {
        checkNotNull(clazz);
        checkNotNull(fieldMap);
        this.clazz = clazz;
        this.columnMap = fieldMap;
        this.gson = gson;
        this.filters.addAll(asList(filters));
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> dataMap = mapper.mapRow(rs, rowNum);
        for(Filter filter : filters) dataMap = filter.apply(dataMap);
        T res = callDefaultConstructor(clazz);
        checkArgument(dataMap.size() == columnMap.size(), "Data map size: '%s' doesn't match field's size: '%s'", dataMap.size(), columnMap.size());
        for(Map.Entry<String, Object> en : dataMap.entrySet()) {
            if(null == en.getValue()) continue;
            Field fi = columnMap.get(en.getKey());
            checkArgument(null != fi, "Canot map field: '%s', class: '%s', columnMap kesy: '%s'", en.getKey(), clazz.getName(), columnMap.keySet());
            if(fi.getType().isAssignableFrom(en.getValue().getClass())) {
                assign(res, fi, en.getValue());
            } else if(null != gson && String.class.isAssignableFrom(en.getValue().getClass())) {
                Object parsed = gson.fromJson((String) en.getValue(), fi.getType());
                assign(res, fi, parsed);
            } else throw new IllegalArgumentException(format(
                    "Cannot map field: '{}', source type: '{}', target type: '{}'", en.getKey(), en.getValue().getClass(), fi.getType()));
        }
        return res;
    }

    private void assign(Object obj, Field fi, Object value) {
        try {
            if (!fi.isAccessible()) fi.setAccessible(true);
            fi.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new UnhandledException(e);
        }
    }

    public interface Filter extends Function<Map<String, Object>, Map<String, Object>>{}
}
