package ru.concerteza.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 4/27/11
 */
public class GuavaUtils {
    // collections
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

    // preconditions
    public static void checkArg(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    // splitter
    public static List<String> split(Splitter splitter, String str) {
        return ImmutableList.copyOf(splitter.split(str));
    }
}
