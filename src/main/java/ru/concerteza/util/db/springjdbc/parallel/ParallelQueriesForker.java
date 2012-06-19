package ru.concerteza.util.db.springjdbc.parallel;

import java.util.List;
import java.util.Map;

/**
 * Must be provided to {@link ParallelQueriesIterator} to divide query parameters between sources
 *
 * @author  alexey
 * Date: 6/8/12
 * @see ParallelQueriesIterator
 */
public interface ParallelQueriesForker {
    /**
     * @param params input query params provided to {@link ParallelQueriesIterator#start(java.util.Map)}
     * @return params list formed from input params
     */
    List<Map<String, ?>> fork(Map<String, ?> params);
}
