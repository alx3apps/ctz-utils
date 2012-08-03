package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Function;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/29/12
 */
public class BeanPropertyFunction implements Function<Object, SqlParameterSource> {
    public static Function<Object, SqlParameterSource> INSTANCE = new BeanPropertyFunction();

    @Override
    public BeanPropertySqlParameterSource apply(@Nullable Object input) {
        checkNotNull(input, "Provided bean is null");
        return new BeanPropertySqlParameterSource(input);
    }
}
