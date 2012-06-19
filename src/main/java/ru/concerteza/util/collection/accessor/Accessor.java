package ru.concerteza.util.collection.accessor;

import java.util.Collection;

/**
 * Read-only collection interface.
 *
 * @author alexey
 * Date: 6/11/12
 * @see RoundRobinAccessor
 */
public interface Accessor<T> extends Collection<T> {
    /**
     * @return collection element, implementation must choose element to return
     */
    T get();
}
