package ru.concerteza.util.collection.maps;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/6/12
 */
public class LowerKeysImmutableMapBuilder<V> extends ImmutableMap.Builder<String, V> {
    public static <V> ImmutableMap.Builder<String, V> builder() {
        return new LowerKeysImmutableMapBuilder<V>();
    }

    public static <V> ImmutableMap<String, V> copyOf(Map<String, V> map) {
        return new LowerKeysImmutableMapBuilder<V>().putAll(map).build();
    }

    @Override
    public ImmutableMap.Builder<String, V> put(String key, V value) {
        checkNotNull(key, "null key");
        super.put(key.toLowerCase(), value);
        return this;
    }

    @Override
    public ImmutableMap.Builder<String, V> putAll(Map<? extends String, ? extends V> map) {
        super.putAll(map);
        return this;
    }
}
