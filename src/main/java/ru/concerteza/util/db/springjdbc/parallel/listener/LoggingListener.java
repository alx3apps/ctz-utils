package ru.concerteza.util.db.springjdbc.parallel.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.concerteza.util.db.springjdbc.parallel.ParallelQueriesListener;

import javax.sql.DataSource;
import java.util.Map;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * Logs incoming event using slf4j logging
 *
 * @author alexey
 * Date: 6/16/12
 * @see ru.concerteza.util.db.springjdbc.parallel.ParallelQueriesListener
 * @see ru.concerteza.util.db.springjdbc.parallel.ParallelQueriesIterator
 */
public class LoggingListener implements ParallelQueriesListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param ds data source
     * @param sql SQL query
     * @param params query input parameters
     */
    @Override
    public void success(DataSource ds, String sql, Map<String, ?> params) {
        logger.info("Data source: '{}', sql: '{}', params: '{}'", new Object[]{ds, sql, params});
    }

    /**
     * @param ds data source
     * @param sql SQL query
     * @param params query input parameters
     * @param ex exception from JDBC
     */
    @Override
    public void error(DataSource ds, String sql, Map<String, ?> params, Throwable ex) {
        logger.error(format("Data source: '{}', sql: '{}', params: '{}'", ds, sql, params), ex);
    }
}
