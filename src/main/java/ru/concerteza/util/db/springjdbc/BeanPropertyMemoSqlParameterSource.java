package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Function;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Wrapper for {@code BeanPropertySqlParameterSource}, retains link to original object
 *
 * @author alexey
 * Date: 9/1/12
 */
public class BeanPropertyMemoSqlParameterSource<T> extends BeanPropertySqlParameterSource {
    public static final Function<Object, SqlParameterSource> BEAN_PROPERTY_MEMO_FUNCTION = new CreateFun();

    private final T object;

    public BeanPropertyMemoSqlParameterSource(T object) {
        super(object);
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("object", object).
                toString();
    }

    private static class CreateFun implements Function<Object, SqlParameterSource> {
        @Override
        public BeanPropertyMemoSqlParameterSource<?> apply(Object input) {
            return new BeanPropertyMemoSqlParameterSource<Object>(input);
        }
    }
}
