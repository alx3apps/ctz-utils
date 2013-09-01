package ru.concerteza.util.collection.maps;

import com.google.common.base.Function;

import java.util.Map;

/**
 * Map to values-collection converter function
 *
 * @author alexkasko
 * Date: 8/31/13
 */
public class MapValueFunction<K, V> implements Function<Map.Entry<K, V>, V> {
    private static final MapValueFunction<?, ?> INSTANCE = new MapValueFunction<Object, Object>();

    /**
     * Generic-friendly wrapper
     *
     * @param <V> map value generic type
     * @return function instance
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Function<? super Map.Entry<K, V>, V> mapValueFunction(Class<K> kclazz, Class<V> vclazz) {
        return (Function) INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V apply(Map.Entry<K, V> input) {
        return input.getValue();
    }
}
