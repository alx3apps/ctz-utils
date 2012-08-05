package ru.concerteza.util.keys;

import javax.annotation.Nullable;

/**
 * Interface for aggregation operations, used in grouping operations
 *
 * @author alexey
 * Date: 7/13/12
 * @see KeyOperations
 */
public interface KeyAggregator<S extends KeyEntry, R> {
    /**
     * Takes source key entry element and last result, returned by this aggregator
     * (null on first call) for stateless implementations. Stateful aggregators may ignore second argument
     *
     * @param s source key entry element
     * @param previous last result, returned by this aggregator
     * @return aggregation result
     */
    R aggregate(S s, @Nullable R previous);
}
