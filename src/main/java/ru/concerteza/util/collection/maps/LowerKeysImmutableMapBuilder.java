package ru.concerteza.util.collection.maps;

import com.google.common.collect.ImmutableMap;

import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable map builder, converts provided keys into lower case. Keys must be locale insensitive.
 *
 * @author  alexey
 * Date: 7/6/12
 */
public class LowerKeysImmutableMapBuilder<V> extends ImmutableMap.Builder<String, V> {
    /**
     * Generic friendly factory method
     *
     * @param <V> map value type
     * @return builder instance
     */
    public static <V> ImmutableMap.Builder<String, V> builder() {
        return new LowerKeysImmutableMapBuilder<V>();
    }

    /**
     * Shortcut copy method
     *
     * @param map map to copy
     * @param <V> map value type
     * @return builder instance
     */
    public static <V> ImmutableMap<String, V> copyOf(Map<String, V> map) {
        return new LowerKeysImmutableMapBuilder<V>().putAll(map).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableMap.Builder<String, V> put(String key, V value) {
        checkNotNull(key, "null key");
        super.put(key.toLowerCase(Locale.ENGLISH), value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableMap.Builder<String, V> putAll(Map<? extends String, ? extends V> map) {
        super.putAll(map);
        return this;
    }
}
