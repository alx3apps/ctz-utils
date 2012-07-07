package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

// won't close RS, for using in resultExtractors
public class ResultSetIterator<T> extends AbstractIterator<T> {
    private final ResultSet rs;
    private final RowMapper<T> mapper;
    private int rowNum = 0;

    public ResultSetIterator(ResultSet rs, RowMapper<T> mapper) {
        checkNotNull(rs, "Provided result set is null");
        checkNotNull(mapper, "Provided mapper set is null");
        this.rs = rs;
        this.mapper = mapper;
    }

    public static <T> ResultSetIterator<T> of(ResultSet rs, RowMapper<T> mapper) {
        return new ResultSetIterator<T>(rs, mapper);
    }

    @Override
    protected T computeNext() {
        try {
            return rs.next() ? mapper.mapRow(rs, rowNum++) : endOfData();
        } catch (SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }
}
