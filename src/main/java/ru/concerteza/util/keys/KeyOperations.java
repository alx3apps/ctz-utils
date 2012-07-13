package ru.concerteza.util.keys;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import ru.concerteza.util.value.Holder;

import javax.annotation.Nullable;
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
    @SuppressWarnings("unchecked")
    public static <S extends KeyEntry, R> Collection<R> groupByKey(Iterator<S> source, KeyAggregator<S, R> aggregator) {
        // holder to prevent tree traversal on update
        TreeMap<String, Holder<R>> map = new TreeMap<String, Holder<R>>();
        while (source.hasNext()) {
            S s = source.next();
            final Holder<R> existed = map.get(s.key());
            if(null == existed) {
                R r = aggregator.aggregate(s, null);
                Holder<R> created = new Holder<R>(r);
                map.put(s.key(), created);
            } else {
                R r = aggregator.aggregate(s, existed.get());
                existed.set(r);
            }
        }
        return Collections2.transform(map.values(), new UnholderFun<R>());
    }

    private static class UnholderFun<T> implements Function<Holder<T>, T> {
        @Override
        public T apply(Holder<T> input) {
            return input.get();
        }
    }
}
