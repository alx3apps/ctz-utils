package ru.concerteza.util.db.springjdbc;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 2/9/12
 */
public class RowMapperFunction<T> extends AbstractResultSetFunction<T> {
    private final RowMapper<T> mapper;

    private RowMapperFunction(RowMapper<T> mapper) {
        this.mapper = mapper;
    }

    public static <T> RowMapperFunction<T> of(RowMapper<T> mapper) {
        return new RowMapperFunction<T>(mapper);
    }

    @Override
    protected T doApply(ResultSet input) throws SQLException {
        return mapper.mapRow(input, 0);
    }
}
