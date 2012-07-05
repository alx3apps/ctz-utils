package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang.UnhandledException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import ru.concerteza.util.db.springjdbc.entitymapper.choosers.SingleClassChooser;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static ru.concerteza.util.CtzReflectionUtils.invokeMethod;
import static ru.concerteza.util.CtzReflectionUtils.mapToObject;
import static ru.concerteza.util.collection.CtzCollectionUtils.defaultList;

/**
 *
 * //todo update documentaion
 * Implementation of <a href="http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/jdbc/core/RowMapper.html">RowMapper</a>
 * for JPA annotated classes. By default maps row data to all columns marked with {@link javax.persistence.Column} annotation.
 * Data will be extracted from {@link java.sql.ResultSet} using <a href="http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/jdbc/core/ColumnMapRowMapper.html">ColumnMapRowMapper</a>
 * Row data preprocessing is supported through {@link EntityFilter}'s.
 * Supports entity hierarchy mapping - {@link EntityChooser} must be provided to decide what class to instantiate.
 * Custom column->field mapping may be provided.
 * Mapper instances are threadsafe.
 * Will hold reference to class (and its subclasses if {@link EntityChooser} is provided), it may cause problems
 * with hot redeploy, see <a href="http://frankkieviet.blogspot.com/2006/10/classloader-leaks-dreaded-permgen-space.html">this link</a>
 * for details.
 * For usage example see {@link EntityMapperTest}
 *
 * @author alexey
 * Date: 4/14/12
 * @see EntityFilter
 * @see EntityChooser
 * @see ru.concerteza.util.CtzReflectionUtils#columnFieldMap
 * @see SingleEntityMapper
 * @see SubclassesEntityMapper
 */

public class EntityMapper<T> implements RowMapper<T>, Function<ResultSet, T> {
    protected final ColumnMapRowMapper mapper = new ColumnMapRowMapper();
    private final EntityChooser<T> chooser;
    private final Map<String, EntityClass<T>> ecMap;

    protected EntityMapper(EntityChooser<T> chooser, Map<String, EntityClass<T>> ecMap) {
        this.chooser = chooser;
        this.ecMap = ecMap;
    }

    public static <T> EntityMapper<T> forClass(Class<T> clazz, EntityFilter... filters) {
        EntityChooser<T>  chooser = SingleClassChooser.forClass(clazz);
        EntityClass<T> ec = new EntityClass<T>(clazz, asList(filters));
        Map<String, EntityClass<T>> ecMap = ImmutableMap.of(clazz.getName(), ec);
        return new EntityMapper<T>(chooser, ecMap);
    }

    public static <T> Builder<T> builder(EntityChooser<T> chooser) {
        return new Builder<T>(chooser);
    }

    @Override
    public T mapRow(ResultSet rs, int notUsed) throws SQLException {
        Map<String, ?> dataMap = mapper.mapRow(rs, 0);
        Class<? extends T> clazz = chooser.choose(dataMap);
        EntityClass<? extends T> ec = ecMap.get(clazz.getName());
        checkArgument(null != ec, "Cannot find entry for chosen subclass: '%s', subclasses: '%s'", clazz, ecMap.keySet());
        for(EntityFilter fi : ec.getFilters()) dataMap = fi.apply(dataMap);
        T res = mapToObject(dataMap, clazz, ec.getColumnMap());
        for(Method me : ec.getPostLoadMethods()) invokeMethod(res, me);
        return res;
    }

    @Override
    public T apply(@Nullable ResultSet input) {
        try {
            return mapRow(input, 0);
        } catch(SQLException e) {
            throw new UnhandledException(e);
        }
    }

    public static class Builder<T> {
        private final EntityChooser<T> chooser;
        private final Map<String, ArrayList<EntityFilter>> filterMap = Maps.newHashMap();

        public Builder(EntityChooser<T> chooser) {
            this.chooser = chooser;
        }

        @SuppressWarnings("unchecked")
        public Builder<T> addFilters(EntityFilter... filters) {
            // type reifying workaround here, don't want additional Class<T> argument
            return addFilters((Class) Object.class, filters);
        }

        public Builder<T> addFilters(Class<? extends T> clazz, EntityFilter... filters) {
            checkNotNull(clazz, "Provided class must be non null");
            checkArgument(filters.length > 0, "At least one filter must be provided");
            if(!filterMap.containsKey(clazz.getName())) filterMap.put(clazz.getName(), new ArrayList<EntityFilter>());
            for(Map.Entry<String, ArrayList<EntityFilter>> en : filterMap.entrySet()) {
                if(isAssignableFromName(clazz, en.getKey())) en.getValue().addAll(asList(filters));
            }
            return this;
        }

        @SuppressWarnings("unchecked") // captured instantiation
        public EntityMapper<T> build() {
            ImmutableMap.Builder<String, EntityClass<T>> builder = ImmutableMap.builder();
            for(Class<? extends T> cl : chooser.subclasses()) {
                List<EntityFilter> filters = defaultList(filterMap.get(cl.getName()));
                builder.put(cl.getName(), new EntityClass(cl, filters));
            }
            return new EntityMapper<T>(chooser, builder.build());
        }

        private boolean isAssignableFromName(Class<?> first, String secondName) {
            try {
                return first.isAssignableFrom(Class.forName(secondName));
            } catch(ClassNotFoundException e) {
                throw new UnhandledException(e);
            }
        }
    }
}
