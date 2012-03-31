package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Predicate;
import org.springframework.jdbc.InvalidResultSetAccessException;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 10/15/11
 */
public abstract class AbstractResultSetPredicate implements Predicate<ResultSet> {
    @Override
    public boolean apply(@Nullable ResultSet input) {
        checkNotNull(input);
        try {
            return doApply(input);
        } catch (SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }

    protected abstract boolean doApply(ResultSet rs) throws SQLException;
}
