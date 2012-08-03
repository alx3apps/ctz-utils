package ru.concerteza.util.db.csv;

import ru.concerteza.util.db.jdbcstub.NoOpPreparedStatement;

import java.sql.*;


/**
 * {@link PreparedStatement} implementation over CSV file with fixed data
 *
 * @author alexey
 * Date: 6/29/12
 */
class CsvPreparedStatement extends NoOpPreparedStatement {
    private final CsvMapIterable iterable;

    /**
     * @param iterable CSV file data iterable
     */
    public CsvPreparedStatement(CsvMapIterable iterable) {
        this.iterable = iterable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet executeQuery() throws SQLException {
        return new MapIteratorResultSet(iterable.iterator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return executeQuery();
    }
}

