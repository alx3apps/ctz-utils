package ru.concerteza.util.db.springjdbc.named;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import ru.concerteza.util.reflect.named.NamedConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Spring's <a href="http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/jdbc/core/RowMapper.html">RowMapper</a>
 * implementation using {@link ru.concerteza.util.reflect.named.NamedConstructor}. Designed to use with immutable classes (with final fields) -
 * only constructor invocation is used without any field access.
 * Constructor arguments must be annotated with JSR330 {@link javax.inject.Named} annotations
 * (there are other ways to access constructor names in runtime,see
 * <a href="http://paranamer.codehaus.org/">paranamer project</a>, but we use {@code @Named} annotations only).
 * Constructors without {@code @Named} annotations on arguments will be ignored.
 * Constructors with {@code @Named} annotations must have all they arguments annotated with not blank values without
 * duplicates. All reflection introspection is done on mapper instantiation. {@code @Named} values used as case-insensitive
 * (databases usually use column names so) so all {@code @Named} values and all result set column names must be locale insensitive.
 * Supports class hierarchies - different subclasses instantiation from single result set based on, use {@link #builder(String)}
 * method to create subclasses mapper and see {@link NamedConstructorSubclassesMapper} for details.
 *
 * @param <T> object type to map data row to
 * @see NamedConstructor_OLD
 * @see ru.concerteza.util.reflect.named.NamedConstructor
 * @see NamedConstructorSingleMapper
 * @see NamedConstructorSubclassesMapper
 */
@Deprecated //use com.alexkasko.springjdbc:springjdbc-constructor-mapper
public abstract class NamedConstructorMapper<T> implements RowMapper<T> {

    /**
     * Factory method for single class named constructor mapper
     *
     * @param clazz class type to instantiate from row data
     * @param <T> class type parameter
     * @return named constructor instance
     */
    public static <T> NamedConstructorMapper<T> forClass(Class<T> clazz) {
        NamedConstructor<T> fun = NamedConstructor.of(clazz);
        return new NamedConstructorSingleMapper<T>(fun);
    }

    /**
     * Builder to create subclasses mapper
     *
     * @param discColumn discriminator
     * @param <T> parent class (or interface) type
     * @return builder instance
     */
    public static <T> Builder<T> builder(String discColumn) {
        return new Builder<T>(discColumn);
    }

    /**
     * Builder class to create subclasses named constructor mapper
     *
     * @param <T>
     */
    public static class Builder<T> {
        private final String discColumn;
        private final ImmutableMap.Builder<String, NamedConstructor<? extends T>> builder = ImmutableMap.builder();

        /**
         * @param discColumn discriminator column
         */
        public Builder(String discColumn) {
            this.discColumn = discColumn;
        }

        /**
         * Registers subclass in mapper
         *
         * @param discriminator discriminator column value
         * @param subclass subclass type
         * @return builder itself
         */
        @SuppressWarnings("unchecked")
        public Builder<T> addSubclass(String discriminator, Class<? extends T> subclass) {
            builder.put(discriminator, NamedConstructor.of(subclass));
            return this;
        }

        /**
         * @return named mapper instance
         */
        public NamedConstructorMapper<T> build() {
            return new NamedConstructorSubclassesMapper<T>(builder.build(), discColumn);
        }
    }

    /**
     * Inner use logging method
     *
     * @param rs result set
     * @param rowNum row number
     * @return current row as string
     * @throws SQLException
     */
    protected String logRS(ResultSet rs, int rowNum) throws SQLException {
        return new ColumnMapRowMapper().mapRow(rs, rowNum).toString();
    }
}
