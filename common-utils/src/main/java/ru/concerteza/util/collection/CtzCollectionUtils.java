package ru.concerteza.util.collection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 6/9/11
 */
public class CtzCollectionUtils {
    public static final Map<String, Object> EMPTY_MAP = ImmutableMap.of();

    // Lists
    public static <F, T> List<T> transformCopy(List<F> fromList, Function<? super F, ? extends T> function) {
        return ImmutableList.copyOf(Lists.transform(fromList, function));
    }

    public static <T> List<T> filterCopy(List<T> unfiltered, Predicate<? super T> predicate) {
        return ImmutableList.copyOf(Iterables.filter(unfiltered, predicate));
    }

    public static <T> List<T> filterCopy(List<?> unfiltered, Class<T> type) {
        return ImmutableList.copyOf(Iterables.filter(unfiltered, type));
    }

    // Sets
    public static <F, T> Set<T> transformCopy(Set<F> fromSet, Function<? super F, ? extends T> function) {
        return ImmutableSet.copyOf(Collections2.transform(fromSet, function));
    }

    public static <T> Set<T> filterCopy(Set<T> unfiltered, Predicate<? super T> predicate) {
        return ImmutableSet.copyOf(Iterables.filter(unfiltered, predicate));
    }

    public static <T> Set<T> filterCopy(Set<?> unfiltered, Class<T> type) {
        return ImmutableSet.copyOf(Iterables.filter(unfiltered, type));
    }
}
