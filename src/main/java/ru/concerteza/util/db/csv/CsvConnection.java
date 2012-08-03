package ru.concerteza.util.db.csv;

import ru.concerteza.util.db.jdbcstub.AbstractConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * {@link java.sql.Connection} implementation for CSV file with fixed data
 *
 * @author alexey
 * Date: 6/29/12
 */
class CsvConnection extends AbstractConnection {
    private final CsvMapIterable iterable;

    /**
     * @param iterable CSV data iterable
     */
    public CsvConnection(CsvMapIterable iterable) {
        this.iterable = iterable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement() throws SQLException {
        return new CsvPreparedStatement(iterable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new CsvPreparedStatement(iterable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SQLException {
        // noop
    }
}
