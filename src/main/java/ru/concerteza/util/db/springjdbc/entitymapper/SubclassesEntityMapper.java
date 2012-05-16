package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.base.Function;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.concerteza.util.CtzReflectionUtils.columnFieldMap;
import static ru.concerteza.util.CtzReflectionUtils.mapToObject;
import static ru.concerteza.util.collection.CtzCollectionUtils.keySetToMap;

/**
 * User: alexey
 * Date: 5/16/12
 */
class SubclassesEntityMapper<T> extends EntityMapper<T> {
    private final ColumnMapFun columnMapFun = new ColumnMapFun();
    private final SubclassChooser<T> chooser;
    private final Map<Class<? extends T>, Map<String, Field>> classColumnMap;

    protected SubclassesEntityMapper(SubclassChooser<T> chooser, Filter... filters) {
        super(filters);
        this.chooser = chooser;
        this.classColumnMap = keySetToMap(chooser.subclasses(), columnMapFun);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> dataMap = mapper.mapRow(rs, rowNum);
        for (Filter fi : filters) dataMap = fi.apply(dataMap);
        Class<? extends T> clazz = chooser.choose(dataMap);
        Map<String, Field> columnMap = classColumnMap.get(clazz);
        checkArgument(null != columnMap, "Cannot find column map for chosen subclass: '%s', subclasses: '%s'", clazz, classColumnMap.keySet());
        return mapToObject(dataMap, clazz, columnMap);
    }

    private class ColumnMapFun implements Function<Class<? extends T>, Map<String, Field>> {
        @Override
        public Map<String, Field> apply(Class<? extends T> input) {
            return columnFieldMap(input);
        }
    }
}
