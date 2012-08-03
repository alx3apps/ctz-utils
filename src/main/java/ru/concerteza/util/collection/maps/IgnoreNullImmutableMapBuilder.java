package ru.concerteza.util.collection.maps;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Immutable map builder, ignores input null values.
 * Solution for <a href="http://code.google.com/p/google-collections/issues/detail?id=234">this problem</a>
 *
 * @author alexey
 * Date: 8/19/11
 */
public class IgnoreNullImmutableMapBuilder<K, V> extends ImmutableMap.Builder<K, V> {

    /**
     * Generic friendly factory method
     *
     * @param <K> map key type
     * @param <V> map value type
     * @return builder instance
     */
    public static <K, V> ImmutableMap.Builder<K, V> builder() {
        return new IgnoreNullImmutableMapBuilder<K, V>();
    }

    /**
     * @param map map to copy
     * @param <K> key type
     * @param <V> value type
     * @return immutable map
     */
    public static <K, V> ImmutableMap<K, V> copyOf(Map<K, V> map) {
        return new IgnoreNullImmutableMapBuilder<K, V>().putAll(map).build();
    }

    /**
     * @param key map key
     * @param value map value, will be ignored if null
     * @return builder itself
     */
    @Override
    public ImmutableMap.Builder<K, V> put(K key, V value) {
        if(null != value) {
            super.put(key, value);
        }
        return this;
    }

    /**
     * @param map map to add
     * @return builder itself
     */
    @Override
    public ImmutableMap.Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
        super.putAll(map);
        return this;
    }
}
