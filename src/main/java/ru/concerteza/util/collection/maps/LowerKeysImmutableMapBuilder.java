package ru.concerteza.util.collection.maps;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ImmutableMap builder, converts keys to lower case
 *
 * @author alexey
 * Date: 7/6/12
 */
public class LowerKeysImmutableMapBuilder<V> extends ImmutableMap.Builder<String, V> {

    /**
     * Generic friendly factory method
     *
     * @param <V> value type
     * @return builder instance
     */
    public static <V> ImmutableMap.Builder<String, V> builder() {
        return new LowerKeysImmutableMapBuilder<V>();
    }

    /**
     * @param map map to copy
     * @param <V> value type
     * @return immutable map
     */
    public static <V> ImmutableMap<String, V> copyOf(Map<String, V> map) {
        return new LowerKeysImmutableMapBuilder<V>().putAll(map).build();
    }

    /**
     * @param key map key, will be converted to lower case
     * @param value map value, must be non null
     * @return builder itself
     */
    @Override
    public ImmutableMap.Builder<String, V> put(String key, V value) {
        checkNotNull(key, "null key");
        super.put(key.toLowerCase(), value);
        return this;
    }

    /**
     * @param map map to put to builder
     * @return builder itself
     */
    @Override
    public ImmutableMap.Builder<String, V> putAll(Map<? extends String, ? extends V> map) {
        super.putAll(map);
        return this;
    }
}
