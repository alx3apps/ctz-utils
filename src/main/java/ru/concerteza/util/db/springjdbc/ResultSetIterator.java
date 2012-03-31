package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.AbstractIterator;
import org.springframework.jdbc.InvalidResultSetAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

// ! not even try to call next() or close() on rs got from iterator
// won't close RS, for using in resultExtractors
public class ResultSetIterator<T> extends AbstractIterator<T> {

    private final ResultSet rs;
    private final Function<ResultSet, T> mapper;

    public static ResultSetIterator<ResultSet> identity(ResultSet rs) {
        return new ResultSetIterator<ResultSet>(rs, Functions.<ResultSet>identity());
    }

    public ResultSetIterator(ResultSet rs, Function<ResultSet, T> mapper) {
        this.rs = rs;
        this.mapper = mapper;
    }

    @Override
    protected T computeNext() {
        try {
            return rs.next() ? mapper.apply(rs) : endOfData();
        } catch (SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }
}
