package ru.concerteza.util.collection;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Immutable map builder, that ignores input null values.
 * Solution for <a href="http://code.google.com/p/google-collections/issues/detail?id=234">this problem</a>
 *
 * @author alexey
 * Date: 8/19/11
 */
public class IgnoreNullImmutableMapBuilder<K, V> extends ImmutableMap.Builder<K, V> {
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
