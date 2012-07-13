package ru.concerteza.util.keys;

import javax.annotation.Nullable;

/**
 * User: alexey
 * Date: 7/13/12
 */
public interface KeyAggregator<S extends KeyEntry, R> {
    R aggregate(S s, @Nullable R previous);
}
