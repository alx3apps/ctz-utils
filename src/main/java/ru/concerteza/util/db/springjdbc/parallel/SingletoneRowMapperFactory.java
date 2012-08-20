package ru.concerteza.util.db.springjdbc.parallel;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Transparent holder of provided row mapper.
 *
 * @author alexey
 * Date: 8/18/12
 */
class SingletoneRowMapperFactory<T> implements RowMapperFactory<T> {
    private final RowMapper<T> singletone;

    /**
     * @param singletone singletone row mapper
     */
    SingletoneRowMapperFactory(RowMapper<T> singletone) {
        checkNotNull(singletone, "Provided row mapper is null");
        this.singletone = singletone;
    }

    /**
     * Generic-friendly constructor method
     *
     * @param singletone singletone row mapper
     * @param <T> mapper result type
     * @return factory instance
     */
    static <T> SingletoneRowMapperFactory<T> of(RowMapper<T> singletone) {
        return new SingletoneRowMapperFactory<T>(singletone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RowMapper<T> produce(SqlParameterSource params) {
        return singletone;
    }
}
