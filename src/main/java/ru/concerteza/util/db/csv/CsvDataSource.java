//package ru.concerteza.util.db.csv;
//
//import ru.concerteza.util.CtzConstants;
//import ru.concerteza.util.db.jdbcstub.AbstractDataSource;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//
///**
// * User: alexey
// * Date: 6/29/12
// */
//
//@Deprecated
//public class CsvDataSource extends AbstractDataSource {
//    private final CsvMapIterable iterable;
//
//    public CsvDataSource(String resourcePath, String splitter) {
//        this(resourcePath, splitter, CtzConstants.UTF8);
//    }
//
//    public CsvDataSource(String resourcePath, String splitter, String encoding) {
//        this.iterable = new CsvMapIterable(resourcePath, splitter, encoding);
//    }
//
//    @Override
//    public Connection getConnection() throws SQLException {
//        return new CsvConnection(iterable);
//    }
//
//    @Override
//    public Connection getConnection(String username, String password) throws SQLException {
//        return getConnection();
//    }
//}
