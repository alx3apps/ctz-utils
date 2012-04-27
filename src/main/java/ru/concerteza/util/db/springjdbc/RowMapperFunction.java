package ru.concerteza.util.db.springjdbc;

import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import ru.concerteza.util.collection.IgnoreNullImmutableMapBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * User: alexey
 * Date: 2/9/12
 */
public class RowMapperFunction<T> extends AbstractResultSetMapper<T> {
    private final RowMapper<T> mapper;

    private RowMapperFunction(RowMapper<T> mapper) {
        this.mapper = mapper;
    }

    public RowMapperFunction<T> wrap(RowMapper<T> mapper) {
        return new RowMapperFunction<T>(mapper);
    }

    @Override
    protected T doApply(ResultSet input) throws SQLException {
        return mapper.mapRow(input, 0);
    }
}
