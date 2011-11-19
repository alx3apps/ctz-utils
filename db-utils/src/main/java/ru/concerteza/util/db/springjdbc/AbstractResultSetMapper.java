package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Function;
import org.springframework.jdbc.InvalidResultSetAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 10/15/11
 */
public abstract class AbstractResultSetMapper<T> implements Function<ResultSet, T> {
    @Override
    public T apply(ResultSet input) {
        checkNotNull(input);
        try {
            return doApply(input);
        } catch (SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }

    protected abstract T doApply(ResultSet input) throws SQLException;
}

