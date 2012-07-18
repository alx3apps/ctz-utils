package ru.concerteza.util.keys;

import com.google.common.collect.Multimap;

import java.util.*;

/**
 * User: alexey
 * Date: 7/13/12
 */
public class KeyOperations {
    public static <S extends KeyEntry, T extends KeyEntry, R> Iterator<R> mergeJoin(
            Iterator<S> source, Iterator<T> target, KeyJoiner<S, T, R> joiner) {
        return new MergeJoinIterator<S, T, R>(source, target, joiner);
    }

    public static <S extends KeyEntry, T extends KeyEntry, R> Iterator<R> mergeLeftJoin(
            Iterator<S> source, Iterator<T> target, KeyJoiner<S, T, R> joiner) {
        return new MergeLeftJoinIterator<S, T, R>(source, target, joiner);
    }

    public static <S extends KeyEntry, T extends KeyEntry, R> Iterator<R> nestedLoopJoin(
            Iterator<S> source, Iterable<T> target, KeyJoiner<S, T, R> joiner) {
        return new NestedLoopJoinIterator<S, T, R>(source, target, joiner);
    }

    public static <S extends KeyEntry, T extends KeyEntry, R> Iterator<R> nestedLoopLeftJoin(
            Iterator<S> source, Iterable<T> target, KeyJoiner<S, T, R> joiner) {
        return new NestedLoopLeftJoinIterator<S, T, R>(source, target, joiner);
    }

    public static <S extends KeyEntry, T, R> Iterator<R> hashJoin(
            Iterator<S> source, Multimap<String, T> target, KeyJoiner<S, T, R> joiner) {
        return new HashJoinIterator<S, T, R>(source, target, joiner);
    }

    public static <S extends KeyEntry, T, R> Iterator<R> hashLeftJoin(
            Iterator<S> source, Multimap<String, T> target, KeyJoiner<S, T, R> joiner) {
        return new HashLeftJoinIterator<S, T, R>(source, target, joiner);
    }

    // returns sorted collection
    public static <S extends KeyEntry, R> Collection<R> groupByKey(Iterator<S> source, KeyAggregator<S, R> aggregator) {
        return new GroupByKeyCollection<S, R>(source, aggregator);
    }

    public static <S extends KeyEntry, R> Iterator<R> groupOrderedByKey(Iterator<S> source, KeyAggregator<S, R> aggregator) {
        return new GroupOrderedByKeyIterator<S, R>(source, aggregator);
    }

}
