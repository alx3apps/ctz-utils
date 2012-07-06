package ru.concerteza.util.db.csv;

import ru.concerteza.util.db.jdbcstub.NoOpPreparedStatement;

import java.sql.*;


/**
* User: alexey
* Date: 6/29/12
*/

class CsvPreparedStatement extends NoOpPreparedStatement {
    private final CsvMapIterable iterable;

    public CsvPreparedStatement(CsvMapIterable iterable) {
        this.iterable = iterable;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return new MapIteratorResultSet(iterable.iterator());
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return executeQuery();
    }
}

