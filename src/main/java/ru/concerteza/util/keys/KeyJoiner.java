package ru.concerteza.util.keys;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: alexey
 * Date: 7/13/12
 */
public interface KeyJoiner<S extends KeyEntry, T, R> {
    R join(S source, @Nullable T target);
}
