package ru.concerteza.util.db.springjdbc.parallel;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Spring's row mappers haven't access to query params.
 * This class may be used if such access is needed on result set mapping.
 *
 * @author alexey
 * Date: 8/18/12
 * @see ParallelQueriesIterator
 */
@Deprecated //use com.alexkasko.springjdbc:parallel-queries
public interface RowMapperFactory<T> {
    /**
     * Must produce row mapper. Will be called by {@link ParallelQueriesIterator} before every SQL query
     *
     * @param params query params
     * @return row mapper for query results
     */
    RowMapper<T> produce(SqlParameterSource params);
}
