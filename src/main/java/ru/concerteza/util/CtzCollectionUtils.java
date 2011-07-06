package ru.concerteza.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 6/9/11
 */
public class CtzCollectionUtils {
    public static <F, T> List<T> transformListCopy(List<F> fromList, Function<? super F, ? extends T> function) {
        return ImmutableList.copyOf(Lists.transform(fromList, function));
    }

    public static <F, T> Set<T> transformSetCopy(List<F> fromList, Function<? super F, ? extends T> function) {
        return ImmutableSet.copyOf(Lists.transform(fromList, function));
    }

    public static <T> List<T> filterList(final List<T> unfiltered, final Predicate<? super T> predicate) {
        return ImmutableList.copyOf(Iterables.filter(unfiltered, predicate));
    }

    public static <T> List<T> filterList(final List<?> unfiltered, final Class<T> type) {
        return ImmutableList.copyOf(Iterables.filter(unfiltered, type));
    }

    public static <K, V> Map<K, V> createMap(Object... pairs) {
        return createMapVararg(pairs);
    }

    private static <K, V> Map<K, V> createMapVararg(Object... paires) {
        return (Map<K, V>) createMapArray(paires);
    }

    private static Map<Object, Object> createMapArray(Object... paires) {
        if(0 != paires.length % 2) throw new IllegalArgumentException(format("Varargs must have even lenth, but was: {}", paires.length));
        ImmutableMap.Builder<Object, Object> builder = new ImmutableMap.Builder<Object, Object>();
        int even = 0;
        int odd = 1;
        while(odd < paires.length) {
            builder.put(paires[even], paires[odd]);
            even += 2;
            odd +=2;
        }
        return builder.build();
    }
}
