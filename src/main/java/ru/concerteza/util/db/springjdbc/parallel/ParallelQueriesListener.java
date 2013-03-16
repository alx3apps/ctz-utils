package ru.concerteza.util.db.springjdbc.parallel;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;

/**
 * Implementors must be registered on iterator using {@link ParallelQueriesIterator#addListener(ParallelQueriesListener)}
 * method. They will be called on every successful and every errored query from different worker threads.
 * Must be thread-safe.
 *
 * @author  alexey
 * Date: 6/16/12
 * @see ParallelQueriesIterator
 */
@Deprecated //use com.alexkasko.springjdbc:parallel-queries
public interface ParallelQueriesListener {
    /**
     * @param ds data source
     * @param sql SQL query
     * @param params query input parameters
     */
    void success(DataSource ds, String sql, SqlParameterSource params);

    /**
     * @param ds data source
     * @param sql SQL query
     * @param params query input parameters
     * @param ex exception from JDBC
     */
    void error(DataSource ds, String sql, SqlParameterSource params, Throwable ex);
}
