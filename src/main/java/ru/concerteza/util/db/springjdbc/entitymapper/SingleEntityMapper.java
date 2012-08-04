package ru.concerteza.util.db.springjdbc.entitymapper;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static ru.concerteza.util.reflect.CtzReflectionUtils.invokeMethod;
import static ru.concerteza.util.reflect.CtzReflectionUtils.mapToObject;

/**
 * Entity mapper implementation for single entity class
 *
 * @author alexey
 * Date: 8/4/12
 * @see EntityMapper
 */
class SingleEntityMapper<T> extends EntityMapper<T> {
    private final EntityClass<T> ec;

    /**
     * @param ec entity class
     */
    SingleEntityMapper(EntityClass<T> ec) {
        this.ec = ec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, ?> dataMap = mapper.mapRow(rs, 0);
        for(EntityFilter fi : ec.getFilters()) dataMap = fi.apply(dataMap);
        T res = mapToObject(dataMap, ec.getClazz(), ec.getColumnMap());
        for(Method me : ec.getPostLoadMethods()) invokeMethod(res, me);
        return res;
    }
}
