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

    // fire transform chain for iters with nullable elements
    public static <T> void fireTransform(Iterator<T> iter) {
        while (iter.hasNext()) iter.next();
    }

    public static <T> void fireTransform(Iterable<T> iter) {
        fireTransform(iter.iterator());
    }

    // Lists
    @Deprecated // use ImmutableList.copyOf manually
    public static <F, T> List<T> transformCopy(List<F> fromList, Function<? super F, ? extends T> function) {
        return ImmutableList.copyOf(Lists.transform(fromList, function));
    }

    @Deprecated // use ImmutableList.copyOf manually
    public static <T> List<T> filterCopy(List<T> unfiltered, Predicate<? super T> predicate) {
        return ImmutableList.copyOf(Iterables.filter(unfiltered, predicate));
    }

    @Deprecated // use ImmutableList.copyOf manually
    public static <T> List<T> filterCopy(List<?> unfiltered, Class<T> type) {
        return ImmutableList.copyOf(Iterables.filter(unfiltered, type));
    }

    // Sets
    @Deprecated // use ImmutableList.copyOf manually
    public static <F, T> Set<T> transformCopy(Set<F> fromSet, Function<? super F, ? extends T> function) {
        return ImmutableSet.copyOf(Collections2.transform(fromSet, function));
    }

    @Deprecated // use ImmutableList.copyOf manually
    public static <T> Set<T> filterCopy(Set<T> unfiltered, Predicate<? super T> predicate) {
        return ImmutableSet.copyOf(Iterables.filter(unfiltered, predicate));
    }

    @Deprecated // use ImmutableList.copyOf manually
    public static <T> Set<T> filterCopy(Set<?> unfiltered, Class<T> type) {
        return ImmutableSet.copyOf(Iterables.filter(unfiltered, type));
    }
}
