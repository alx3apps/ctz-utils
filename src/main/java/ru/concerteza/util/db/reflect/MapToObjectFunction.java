package ru.concerteza.util.db.reflect;

import com.google.common.base.Function;
import ru.concerteza.util.CtzReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.concerteza.util.CtzReflectionUtils.columnFieldMap;

/**
 * User: alexey
 * Date: 6/30/12
 */
public class MapToObjectFunction<T> implements Function<Map<String, ?>, T> {
    private final Class<T> clazz;
    private final Map<String, Field> fieldMap;

    protected MapToObjectFunction(Class<T> clazz) {
        this(clazz, columnFieldMap(clazz));
    }

    public MapToObjectFunction(Class<T> clazz, Map<String, Field> fieldMap) {
        checkNotNull(clazz, "Input class must be non null");
        checkNotNull(fieldMap, "Input fieldMap must be non null");
        this.clazz = clazz;
        this.fieldMap = fieldMap;
    }

    public static <T> MapToObjectFunction<T> forClass(Class<T> clazz) {
        return new MapToObjectFunction<T>(clazz);
    }

    @Override
    public T apply(@Nullable Map<String, ?> input) {
        checkNotNull(input, "Input map must be non null");
        return CtzReflectionUtils.mapToObject(input, clazz, fieldMap);
    }
}
