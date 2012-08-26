package ru.concerteza.util.keys;

import javax.annotation.Nullable;

/**
 * Interface for functions that join entries with matched keys
 *
 * @author alexey
 * Date: 7/13/12
 */
public interface KeyJoiner<S, T, R> {
    /**
     * May implement any join logic
     *
     * @param source source key entry
     * @param target target entry
     * @return any join result
     */
    R join(S source, @Nullable T target);
}
