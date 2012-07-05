package ru.concerteza.util.db.reflect;

import com.google.common.base.Function;
import ru.concerteza.util.CtzReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.concerteza.util.CtzReflectionUtils.columnFieldMap;
import static ru.concerteza.util.CtzReflectionUtils.fieldGettersMap;
import static ru.concerteza.util.CtzReflectionUtils.objectToMap;

/**
 * User: alexey
 * Date: 6/30/12
 */
public class ObjectToMapFunction implements Function<Object, Map<String, ?>> {
    private final Map<String, Method> getterMap;

    public ObjectToMapFunction(Class<?> clazz) {
        this(fieldGettersMap(clazz, columnFieldMap(clazz)));
    }

    public ObjectToMapFunction(Map<String, Method> getterMap) {
        checkNotNull(getterMap, "Input getterMap must be non null");
        this.getterMap = getterMap;
    }

    @Override
    public Map<String, ?> apply(@Nullable Object input) {
        checkNotNull(input, "Input object must be non null");
        return objectToMap(input, getterMap);
    }
}
