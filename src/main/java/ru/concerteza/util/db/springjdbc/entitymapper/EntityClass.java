package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import ru.concerteza.util.CtzReflectionUtils;

import javax.persistence.PostLoad;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static ru.concerteza.util.CtzReflectionUtils.columnFieldMap;

/**
 * User: alexey
 * Date: 6/23/12
 */
class EntityClass<T> {
    private final Class<T> clazz;
    private final Map<String, Field> columnMap;
    private final List<EntityFilter> filters;
    private final List<Method> postLoadMethods;

    EntityClass(Class<T> clazz, List<EntityFilter> filters) {
        this.clazz = clazz;
        this.columnMap = columnFieldMap(clazz);
        this.filters = ImmutableList.copyOf(filters);
        this.postLoadMethods = ImmutableList.copyOf(CtzReflectionUtils.collectMethods(clazz, PostLoadPredicate.INSTANCE));
    }

    Class<T> getClazz() {
        return clazz;
    }

    Map<String, Field> getColumnMap() {
        return columnMap;
    }

    public List<EntityFilter> getFilters() {
        return filters;
    }

    List<Method> getPostLoadMethods() {
        return postLoadMethods;
    }

    private enum PostLoadPredicate implements Predicate<Method> {
        INSTANCE;

        @Override
        public boolean apply(Method input) {
            if(null != input.getAnnotation(PostLoad.class)) {
                checkArgument(0 == input.getParameterTypes().length,
                        "Post load method must have no parameters, method: '%s'", input.getName());
                return true;
            }
            return false;
        }
    }
}
