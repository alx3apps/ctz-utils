package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang.UnhandledException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static ru.concerteza.util.collection.CtzCollectionUtils.defaultList;

/**
 *
 * Implementation of <a href="http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/jdbc/core/RowMapper.html">RowMapper</a>
 * for JPA annotated classes. By default maps row data to all columns marked with {@link javax.persistence.Column} annotation.
 * Data will be extracted from {@link java.sql.ResultSet} using <a href="http://static.springsource.org/spring/docs/3.1.x/javadoc-api/org/springframework/jdbc/core/ColumnMapRowMapper.html">ColumnMapRowMapper</a>
 * Row data preprocessing is supported (on per subclass basis) through {@link EntityFilter}'s. Entity post load operations are supported
 * through entity methods marked as {@link javax.persistence.PostLoad}.
 * Supports entity hierarchy mapping - {@link Builder} mast be used to construct hierarhy mappers.
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
 * @see SingleEntityMapper
 * @see SubclassesEntityMapper
 */

public abstract class EntityMapper<T> implements RowMapper<T> {
    protected final ColumnMapRowMapper mapper = new ColumnMapRowMapper();

    /**
     * Single entity class factory method
     *
     * @param clazz class of target entity
     * @param filters list of preprocessing filters
     * @param <T> entity class param
     * @return entity mapper instance
     */
    public static <T> EntityMapper<T> forClass(Class<T> clazz, EntityFilter... filters) {
        EntityClass<T> ec = new EntityClass<T>(clazz, asList(filters));
        return new SingleEntityMapper<T>(ec);
    }

    /**
     * Method for hierarchy mapping
     *
     * @param chooser entity class chooser instance
     * @param <T> superclass type
     * @return builder instance
     */
    public static <T> Builder<T> builder(EntityChooser<T> chooser) {
        return new Builder<T>(chooser);
    }

    /**
     * Builder class for subclasses mapping
     *
     * @param <T> superclass type
     */
    public static class Builder<T> {
        private final EntityChooser<T> chooser;
        private final Map<String, ArrayList<EntityFilter>> filterMap = Maps.newHashMap();

        /**
         * @param chooser subclasses chooser
         */
        public Builder(EntityChooser<T> chooser) {
            this.chooser = chooser;
        }

        /**
         * Adds filters? that will be applied for all possible subclasses
         *
         * @param filters preprocessing filters
         * @return builder itself
         */
        @SuppressWarnings("unchecked")
        public Builder<T> addFilters(EntityFilter... filters) {
            // type reifying workaround here, don't want additional Class<T> argument
            return addFilters((Class) Object.class, filters);
        }

        /**
         * Add filters for specific subclass
         *
         * @param clazz superclass (or interface), that subclasses must inherit (implement) for filters to be applied
         * @param filters preprocessing filters
         * @return builder itself
         */
        public Builder<T> addFilters(Class<? extends T> clazz, EntityFilter... filters) {
            checkNotNull(clazz, "Provided class must be non null");
            checkArgument(filters.length > 0, "At least one filter must be provided");
            if(!filterMap.containsKey(clazz.getName())) filterMap.put(clazz.getName(), new ArrayList<EntityFilter>());
            for(Map.Entry<String, ArrayList<EntityFilter>> en : filterMap.entrySet()) {
                if(isAssignableFromName(clazz, en.getKey())) en.getValue().addAll(asList(filters));
            }
            return this;
        }

        /**
         * @return entity mapper instance
         */
        @SuppressWarnings("unchecked") // captured instantiation
        public EntityMapper<T> build() {
            ImmutableMap.Builder<String, EntityClass<T>> builder = ImmutableMap.builder();
            for(Class<? extends T> cl : chooser.subclasses()) {
                List<EntityFilter> filters = defaultList(filterMap.get(cl.getName()));
                builder.put(cl.getName(), new EntityClass(cl, filters));
            }
            return new SubclassesEntityMapper<T>(chooser, builder.build());
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
