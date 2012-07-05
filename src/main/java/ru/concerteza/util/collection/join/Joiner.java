package ru.concerteza.util.collection.join;

/**
 * User: alexey
 * Date: 7/4/12
 */
public interface Joiner<S, T, R> {
    public R join(S source, T target);
}
