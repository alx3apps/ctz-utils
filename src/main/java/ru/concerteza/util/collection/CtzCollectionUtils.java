package ru.concerteza.util.collection;

import com.google.common.base.Function;
import com.google.common.collect.*;
import ru.concerteza.util.collection.finishable.FinishableFunction;
import ru.concerteza.util.collection.finishable.FinishableIterator;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Collection utilities
 *
 * @author alexey
 * Date: 6/9/11
 * @see CtzCollectionUtilsTest
 */
public class CtzCollectionUtils {
    @Deprecated // use ImmutableMap.of() directly
    public static final Map<String, Object> EMPTY_MAP = ImmutableMap.of();

    /**
     * Fires guava transform chain for iterators, which may produce nullable elements.
     * Will be replaced by {@code Iterators.advance(...)} in newer guava versions
     *
     * @param iter input iterator
     * @return count of transformed elements
     */
    public static long fireTransform(Iterator<?> iter) {
        int counter = 0;
        while (iter.hasNext()) {
            iter.next();
            counter +=1;
        }
        return counter;
    }

    /**
     * Fires guava transform chain for iterables, which may produce nullable elements
     *
     * @param iter input iterable
     * @return count of transformed elements
     */
    public static long fireTransform(Iterable<?> iter) {
        return fireTransform(iter.iterator());
    }

    /**
     * Produces map from key set, values are function products. Inspired by
     * <a href="http://docs.guava-libraries.googlecode.com/git-history/v12.0/javadoc/com/google/common/collect/Maps.html#uniqueIndex%28java.util.Iterator,%20com.google.common.base.Function%29">uniqueIndex</a> method
     *
     * @param keySet input set, contains maps keys
     * @param valueFunction produces map values based on set keys
     * @param <K> map key type
     * @param <V> map value type
     * @return map based on input set, values are function products
     */
    public static <K, V> ImmutableMap<K, V> keySetToMap(Set<K> keySet, Function<? super K, V> valueFunction) {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for(K key : keySet) {
            builder.put(key, valueFunction.apply(key));
        }
        return builder.build();
    }

    /**
     * Converts separate keys and values lists into immutable map. Lists must have the same size.
     *
     * @param keys keys list
     * @param values values list
     * @param <K> key type
     * @param <V> value type
     * @return immutable map
     */
    public static <K, V> ImmutableMap<K, V> listsToMap(List<K> keys, List<V> values) {
        checkArgument(keys.size() == values.size(), "Keys and values sizes differs, keys: '%s', values: '%s'", keys.size(), values.size());
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for(int i = 0; i < keys.size(); i++) {
            builder.put(keys.get(i), values.get(i));
        }
        return builder.build();
    }

    /**
     * Sentinel wrapper for API's that may return null instead of empty list
     *
     * @param input list or null
     * @param <T> list value type
     * @return empty list on null input, provided list otherwise
     */
    public static <T> List<T> defaultList(@Nullable List<T> input) {
        if(null == input) return ImmutableList.of();
        return input;
    }

    /**
     * Concurrent hash set factory method
     *
     * @param <T> value type
     * @return set backed by {@link ConcurrentHashMap}
     */
    public static <T> Set<T> newConcurrentHashSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
    }

    /**
     * Creates {@link TreeSet} with provided data and comparator
     *
     * @param iter data for tree set
     * @param comp comparator
     * @param <T> element type
     * @return created tree set
     */
    public static <T> TreeSet<T> newTreeSet(Iterator<T> iter, Comparator<T> comp) {
        checkNotNull(iter, "Provided iterator is null");
        checkNotNull(comp, "Provided comparator is null");
        TreeSet<T> res = new TreeSet<T>(comp);
        for(T t : SingleUseIterable.of(iter)) {
            res.add(t);
        }
        return res;
    }

    /**
     * Copies provided map into {@link LinkedHashMap} converting keys to lower case.
     * Keys must be locale insensitive.
     *
     * @param map input map
     * @param <V> map value type
     * @return map with lower case keys
     */
    public static <V> LinkedHashMap<String, V> toLowerKeysMap(Map<String, V> map) {
        checkNotNull(map, "Provided map is null");
        LinkedHashMap<String, V> res = new LinkedHashMap<String, V>(map.size());
        for(Map.Entry<String, V> en : map.entrySet()) {
            V existed = res.put(en.getKey().toLowerCase(Locale.ENGLISH), en.getValue());
            checkArgument(null == existed, "Duplicate key: '%s' after lowering", en.getKey());
        }
        return res;
    }

    /**
     * The same as Guava's {@code Iterators.transform}, additionally calls {@link ru.concerteza.util.collection.finishable.FinishableFunction#finish()}
     * on iterator exhaustion
     *
     * @param fromIterator source iterator
     * @param function finishable function
     * @param <F> from type
     * @param <T> to type
     * @return finishable iterator
     */
    public static <F, T> Iterator<T> transformFinishable(final Iterator<F> fromIterator,
                                                         final FinishableFunction<? super F, ? extends T> function) {
        return FinishableIterator.of(fromIterator, function);
    }
}
