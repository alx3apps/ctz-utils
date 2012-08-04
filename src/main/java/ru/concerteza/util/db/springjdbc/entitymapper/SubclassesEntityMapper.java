package ru.concerteza.util.db.springjdbc.entitymapper;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.concerteza.util.reflect.CtzReflectionUtils.invokeMethod;
import static ru.concerteza.util.reflect.CtzReflectionUtils.mapToObject;

/**
 * Entity mapper implementation for class hierarchy
 *
 * @author alexey
 * Date: 8/4/12
 */
class SubclassesEntityMapper<T> extends EntityMapper<T> {
    private final EntityChooser<T> chooser;
    private final Map<String, EntityClass<T>> ecMap;

    /**
     * @param chooser subclass chooser
     * @param ecMap entity class map
     */
    SubclassesEntityMapper(EntityChooser<T> chooser, Map<String, EntityClass<T>> ecMap) {
        this.chooser = chooser;
        this.ecMap = ecMap;
    }

    /**
     * {@inheritDoc}
	 */
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, ?> dataMap = mapper.mapRow(rs, 0);
        Class<? extends T> clazz = chooser.choose(dataMap);
        EntityClass<? extends T> ec = ecMap.get(clazz.getName());
        checkArgument(null != ec, "Cannot find entry for chosen subclass: '%s', subclasses: '%s'", clazz, ecMap.keySet());
        for(EntityFilter fi : ec.getFilters()) dataMap = fi.apply(dataMap);
        T res = mapToObject(dataMap, clazz, ec.getColumnMap());
        for(Method me : ec.getPostLoadMethods()) invokeMethod(res, me);
        return res;
    }
}
