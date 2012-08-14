package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ru.concerteza.util.reflect.CtzReflectionUtils;

import javax.persistence.Column;
import javax.persistence.PostLoad;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static ru.concerteza.util.reflect.CtzReflectionUtils.collectFields;

/**
 * Holds reflection information about entity class
 *
 * @author alexey
 * Date: 6/23/12
 * @see EntityMapper
 */
class EntityClass<T> {
    private final Class<T> clazz;
    private final Map<String, Field> columnMap;
    private final List<EntityFilter> filters;
    private final List<Method> postLoadMethods;

    /**
     * @param clazz class of target entity
     * @param filters list of preprocessing filters that will be applied to row data
     */
    EntityClass(Class<T> clazz, Collection<EntityFilter> filters) {
        this.clazz = clazz;
        this.columnMap = columnFieldMap(clazz);
        this.filters = ImmutableList.copyOf(filters);
        this.postLoadMethods = ImmutableList.copyOf(CtzReflectionUtils.collectMethods(clazz, PostLoadPredicate.INSTANCE));
    }

    /**
     * @return class of target entity
     */
    Class<T> getClazz() {
        return clazz;
    }

    /**
     * @return column to field map
     */
    Map<String, Field> getColumnMap() {
        return columnMap;
    }

    /**
     * @return list of preprocessing filters
     */
    List<EntityFilter> getFilters() {
        return filters;
    }

    /**
     * @return list of entity post load methods
     */
    List<Method> getPostLoadMethods() {
        return postLoadMethods;
    }

    public Map<String, Field> columnFieldMap(Class<?> clazz) {
        ImmutableMap.Builder<String, Field> builder = new ImmutableMap.Builder<String, Field>();
        for (Field fi : collectFields(clazz, AnnotatedColumnPredicate.INSTANCE)) {
            Column col = fi.getAnnotation(Column.class);
            String name = isNotEmpty(col.name()) ? col.name() : fi.getName();
            builder.put(name.toLowerCase(Locale.ENGLISH), fi);
        }
        return builder.build();
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

    private enum AnnotatedColumnPredicate implements Predicate<Field> {
        INSTANCE;
        @Override
        public boolean apply(Field input) {
            return null != input.getAnnotation(Column.class);
        }
    }
}
