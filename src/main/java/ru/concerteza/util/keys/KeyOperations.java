package ru.concerteza.util.keys;

import com.google.common.collect.Multimap;

import java.util.*;

/**
 * Zero copy, relational operations on keyed entries
 *
 * @author alexey
 * Date: 7/13/12
 * @see KeyEntry
 * @see KeyOperationsTest
 */
public class KeyOperations {
    /**
     * Zero copy, lazy merge join, source and target iterator must be ordered by keys
     *
     * @param source source iterator, must be ordered by key
     * @param target target iterator, must be ordered by key
     * @param joiner joiner instance
     * @param <S> source elements type
     * @param <T> target elements type
     * @param <R> result elements type
     * @return iterator over joined elements
     */
    public static <S extends KeyEntry, T extends KeyEntry, R> Iterator<R> mergeJoin(
            Iterator<S> source, Iterator<T> target, KeyJoiner<S, T, R> joiner) {
        return new MergeJoinIterator<S, T, R>(source, target, joiner);
    }

    /**
     * Zero copy, lazy merge left join, source and target iterator must be ordered by keys
     *
     * @param source source iterator, must be ordered by key
     * @param target target iterator, must be ordered by key
     * @param joiner joiner instance
     * @param <S> source elements type
     * @param <T> target elements type
     * @param <R> result elements type
     * @return iterator over joined elements
     */
    public static <S extends KeyEntry, T extends KeyEntry, R> Iterator<R> mergeLeftJoin(
            Iterator<S> source, Iterator<T> target, KeyJoiner<S, T, R> joiner) {
        return new MergeLeftJoinIterator<S, T, R>(source, target, joiner);
    }

    /**
     * Zero copy, lazy nested loop join
     *
     * @param source source iterator
     * @param target target iterable
     * @param joiner joiner instance
     * @param <S> source elements type
     * @param <T> target elements type
     * @param <R> result elements type
     * @return iterator over joined elements
     */
    public static <S extends KeyEntry, T extends KeyEntry, R> Iterator<R> nestedLoopJoin(
            Iterator<S> source, Iterable<T> target, KeyJoiner<S, T, R> joiner) {
        return new NestedLoopJoinIterator<S, T, R>(source, target, joiner);
    }

    /**
     * Zero copy, lazy nested loop left join
     *
     * @param source source iterator
     * @param target target iterable
     * @param joiner joiner instance
     * @param <S> source elements type
     * @param <T> target elements type
     * @param <R> result elements type
     * @return iterator over joined elements
     */
    public static <S extends KeyEntry, T extends KeyEntry, R> Iterator<R> nestedLoopLeftJoin(
            Iterator<S> source, Iterable<T> target, KeyJoiner<S, T, R> joiner) {
        return new NestedLoopLeftJoinIterator<S, T, R>(source, target, joiner);
    }

    /**
     * Zero copy, lazy hash join
     *
     * @param source source iterator
     * @param target target multimap
     * @param joiner joiner instance
     * @param <S> source elements type
     * @param <T> target elements type
     * @param <R> result elements type
     * @return iterator over joined elements
     */
    public static <S extends KeyEntry, T, R> Iterator<R> hashJoin(
            Iterator<S> source, Multimap<String, T> target, KeyJoiner<S, T, R> joiner) {
        return new HashJoinIterator<S, T, R>(source, target, joiner);
    }

    /**
     * Zero copy, lazy hash left join
     *
     * @param source source iterator
     * @param target target multimap
     * @param joiner joiner instance
     * @param <S> source elements type
     * @param <T> target elements type
     * @param <R> result elements type
     * @return iterator over joined elements
     */
    public static <S extends KeyEntry, T, R> Iterator<R> hashLeftJoin(
            Iterator<S> source, Multimap<String, T> target, KeyJoiner<S, T, R> joiner) {
        return new HashLeftJoinIterator<S, T, R>(source, target, joiner);
    }

    /**
     * Zero copy group by key implementation, eager
     *
     * @param source source iterator
     * @param aggregator aggregator instance
     * @param <S> source type
     * @param <R> result type
     * @return sorted collection of grouped elements
     */
    public static <S extends KeyEntry, R> Collection<R> groupByKey(Iterator<S> source, KeyAggregator<S, R> aggregator) {
        return new GroupByKeyCollection<S, R>(source, aggregator);
    }

    /**
     * Zero copy, lazy group by key implementation
     *
     * @param source source iterator, must be ordered by key
     * @param aggregator aggregator instance
     * @param <S> source type
     * @param <R> result type
     * @return iterator over grouped elements
     */
    public static <S extends KeyEntry, R> Iterator<R> groupOrderedByKey(Iterator<S> source, KeyAggregator<S, R> aggregator) {
        return new GroupOrderedByKeyIterator<S, R>(source, aggregator);
    }

}
