package ru.concerteza.util.db.springjdbc.entitymapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static ru.concerteza.util.CtzReflectionUtils.columnFieldMap;
import static ru.concerteza.util.CtzReflectionUtils.mapToObject;

/**
 * User: alexey
 * Date: 5/16/12
 */
class SingleEntityMapper<T> extends EntityMapper<T> {
    private final Class<T> clazz;
    private final Map<String, Field> columnMap;

    protected SingleEntityMapper(Class<T> clazz, Filter... filters) {
        super(filters);
        this.clazz = clazz;
        this.columnMap = columnFieldMap(clazz);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> dataMap = mapper.mapRow(rs, rowNum);
        for(Filter fi : filters) dataMap = fi.apply(dataMap);
        return mapToObject(dataMap, clazz, columnMap);
    }
}
