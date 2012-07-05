//package ru.concerteza.util.db.csv;
//
//import ru.concerteza.util.db.jdbcstub.AbstractConnection;
//
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Statement;
//
///**
// * User: alexey
// * Date: 6/29/12
// */
//
//@Deprecated
//class CsvConnection extends AbstractConnection {
//    private final CsvMapIterable iterable;
//
//    public CsvConnection(CsvMapIterable iterable) {
//        this.iterable = iterable;
//    }
//
//    @Override
//    public Statement createStatement() throws SQLException {
//        return new CsvPreparedStatement(iterable);
//    }
//
//    @Override
//    public PreparedStatement prepareStatement(String sql) throws SQLException {
//        return new CsvPreparedStatement(iterable);
//    }
//
//    @Override
//    public void close() throws SQLException {
//        // noop
//    }
//}
