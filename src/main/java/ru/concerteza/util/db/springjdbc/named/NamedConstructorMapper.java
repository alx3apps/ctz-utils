package ru.concerteza.util.db.springjdbc.named;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.RowMapper;
import ru.concerteza.util.db.springjdbc.mapper.LowerColumnsIgnoreNullMapper;

import java.util.Map;

public abstract class NamedConstructorMapper<T> implements RowMapper<T> {
    protected final RowMapper<Map<String, ?>> mapper = new LowerColumnsIgnoreNullMapper();

    public static <T> NamedConstructorMapper<T> forClass(Class<T> clazz) {
        NamedConstructorFunction<T> fun = NamedConstructorFunction.forClass(clazz);
        return new NamedConstructorSingleMapper<T>(fun);
    }

    public static <T> Builder<T> builder(String discColumn) {
        return new Builder<T>(discColumn);
    }

    public static class Builder<T> {
        private final String dicsColumn;
        private final ImmutableMap.Builder<String, NamedConstructorFunction<? extends T>> builder = ImmutableMap.builder();

        public Builder(String dicsColumn) {
            this.dicsColumn = dicsColumn;
        }

        public Builder<T> addSubclass(String discriminator, Class<? extends T> subclass) {
            builder.put(discriminator, NamedConstructorFunction.forClass(subclass));
            return this;
        }

        public NamedConstructorMapper<T> build() {
            return new NamedConstructorSubclassesMapper<T>(dicsColumn, builder.build());
        }
    }
}
