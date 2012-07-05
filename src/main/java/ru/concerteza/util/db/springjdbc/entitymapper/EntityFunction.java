package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * User: alexey
 * Date: 6/30/12
 */
public class EntityFunction<T> implements Function<Map<String,?>, T> {
    private final Class<T> clazz;
    private final Map<String, Field> columnMap;

    public EntityFunction(Class<T> clazz, Map<String, Field> columnMap) {
        this.clazz = clazz;
        this.columnMap = columnMap;
    }

    @Override
    public T apply(@Nullable Map<String, ?> input) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
