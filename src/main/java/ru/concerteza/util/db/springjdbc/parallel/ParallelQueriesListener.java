package ru.concerteza.util.db.springjdbc.parallel;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Implementors must be registered on iterator using {@link ParallelQueriesIterator#addListener(ParallelQueriesListener)}
 * method. They will be called on every successful and every errored query from different worker threads.
 * Must be thread-safe.
 *
 * @author  alexey
 * Date: 6/16/12
 * @see ParallelQueriesIterator
 */
public interface ParallelQueriesListener {
    /**
     * @param ds data source
     * @param sql SQL query
     * @param params query input parameters
     */
    void success(DataSource ds, String sql, Map<String, ?> params);

    /**
     * @param ds data source
     * @param sql SQL query
     * @param params query input parameters
     * @param ex exception from JDBC
     */
    void error(DataSource ds, String sql, Map<String, ?> params, Throwable ex);
}
