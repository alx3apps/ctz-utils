package ru.concerteza.util.collection.maps;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ImmutableMap builder, converts keys to lower case and ignores input null values.
 *
 * @author alexey
 * Date: 7/6/12
 */
public class LowerKeysIgnoreNullImmutableMapBuilder<V> extends ImmutableMap.Builder<String, V> {

    /**
     * Generic friendly factory method
     *
     * @param <V> value type
     * @return builder instance
     */
    public static <V> ImmutableMap.Builder<String, V> builder() {
        return new LowerKeysIgnoreNullImmutableMapBuilder<V>();
    }

    /**
     * @param map map to copy
     * @param <V> value type
     * @return immutable map
     */
    public static <V> ImmutableMap<String, V> copyOf(Map<String, V> map) {
        return new LowerKeysIgnoreNullImmutableMapBuilder<V>().putAll(map).build();
    }

    /**
     * Puts key-value pair into builder if value is not null. No-op otherwise.
     *
     * @param key map key
     * @param value map value
     * @return builder itself
     */
    @Override
    public ImmutableMap.Builder<String, V> put(String key, V value) {
        checkNotNull(key, "null key");
        if(null != value) {
            super.put(key.toLowerCase(), value);
        }
        return this;
    }

    /**
     * @param map map to copy data from
     * @return builder itself
     */
    @Override
    public ImmutableMap.Builder<String, V> putAll(Map<? extends String, ? extends V> map) {
        super.putAll(map);
        return this;
    }
}
