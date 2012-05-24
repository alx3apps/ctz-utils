package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.base.Function;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static ru.concerteza.util.CtzReflectionUtils.columnFieldMap;
import static ru.concerteza.util.collection.CtzCollectionUtils.keySetToMap;

/**
 * Implementation of <a href="http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/jdbc/core/RowMapper.html">RowMapper</a>
 * for JPA annotated classes. By default maps row data to all columns marked with {@link javax.persistence.Column} annotation.
 * Data will be extracted from {@link java.sql.ResultSet} using <a href="http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/jdbc/core/ColumnMapRowMapper.html">ColumnMapRowMapper</a>
 * Row data preprocessing is supported through {@link Filter}'s.
 * Supports entity hierarchy mapping - {@link SubclassChooser} must be provided to decide what class to instantiate.
 * Custom column->field mapping may be provided.
 * Mapper instances are threadsafe.
 * Will hold reference to class (and its subclasses if {@link SubclassChooser} is provided), it may cause problems
 * with hot redeploy, see <a href="http://frankkieviet.blogspot.com/2006/10/classloader-leaks-dreaded-permgen-space.html">this link</a>
 * for details.
 * For usage example see {@link EntityMapperTest}
 *
 * @author alexey
 * Date: 4/14/12
 * @see Filter
 * @see SubclassChooser
 * @see ru.concerteza.util.CtzReflectionUtils#columnFieldMap
 * @see SingleEntityMapper
 * @see SubclassesEntityMapper
 */

public abstract class EntityMapper<T> implements RowMapper<T> {
    private static final ColumnMapFun COLUMN_MAP_FUN = new ColumnMapFun();
    protected final ColumnMapRowMapper mapper = new ColumnMapRowMapper();
    protected final List<Filter> filters;

    /**
     * Constructor for inheritors/
     * @param filters Vararg {@link Filter}'s that will be applied to row data before mapping
     */
    protected EntityMapper(Filter... filters) {
        this.filters = asList(filters);
    }

    /**
     * Factory method for single class entity mapper, columnMap will be created using {@link ru.concerteza.util.CtzReflectionUtils#columnFieldMap}
     * @param clazz entity class to instantiate
     * @param filters vararg {@link Filter}'s that will be applied to row data before mapping
     * @param <T> entity class type
     * @return {@link SingleEntityMapper}
     */
    public static <T> EntityMapper<T> forClass(Class<T> clazz, Filter... filters) {
        Map<String, Field> columnMap = columnFieldMap(clazz);
        return new SingleEntityMapper<T>(clazz, columnMap, filters);
    }

    /**
     * Factory method for single class entity mapper
     * @param clazz entity class to instantiate
     * @param columnMap column_name->class_field map
     * @param filters vararg {@link Filter}'s that will be applied to row data before mapping
     * @param <T> entity class type
     * @return {@link SingleEntityMapper}
     */
    public static <T> EntityMapper<T> forClass(Class<T> clazz, Map<String, Field> columnMap, Filter... filters) {
        return new SingleEntityMapper<T>(clazz, columnMap, filters);
    }

    /**
     * Factory method for entity hierarchy mapper, columnMap will be created using {@link ru.concerteza.util.CtzReflectionUtils#columnFieldMap}
     * @param chooser {@link SubclassChooser} defining what class to instantiate
     * @param filters {@link Filter}'s that will be applied to row data before mapping
     * @param <T> parent entity class type
     * @return {@link SubclassesEntityMapper}
     */
    @SuppressWarnings("unchecked")
    public static <T> EntityMapper<T> forSubclasses(SubclassChooser<T> chooser, Filter... filters) {
        Map<Class<? extends T>, Map<String, Field>> classColumnMap = keySetToMap(chooser.subclasses(), COLUMN_MAP_FUN);
        return new SubclassesEntityMapper<T>(chooser, classColumnMap, filters);
    }

    /**
     * Factory method for entity hierarchy mapper
     * @param chooser {@link SubclassChooser} defining what class to instantiate
     * @param classColumnMap column_name->class_field map for each entity in hierarchy
     * @param filters vararg {@link Filter}'s that will be applied to row data before mapping
     * @param <T> parent entity class type
     * @return {@link SubclassesEntityMapper}
     */
    public static <T> EntityMapper<T> forSubclasses(SubclassChooser<T> chooser, Map<Class<? extends T>, Map<String, Field>> classColumnMap, Filter... filters) {
        return new SubclassesEntityMapper<T>(chooser, classColumnMap, filters);
    }

    private static class ColumnMapFun<T> implements Function<Class, Map<String, Field>> {
        @Override
        public Map<String, Field> apply(Class input) {
            return columnFieldMap(input);
        }
    }
}
