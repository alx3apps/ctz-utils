package ru.concerteza.util.collection;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * User: alexey
 * Date: 8/19/11
 */
public class IgnoreNullImmutableMapBuilder<K, V> extends ImmutableMap.Builder<K, V> {
    @Override
    public ImmutableMap.Builder<K, V> put(K key, V value) {
        if(null != value) {
            super.put(key, value);
        }
        return this;
    }

    @Override
    public ImmutableMap.Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
        super.putAll(map);
        return this;
    }
}
