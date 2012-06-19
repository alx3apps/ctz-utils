package ru.concerteza.util.collection;

import com.google.common.base.Function;
import ru.concerteza.util.value.Pair;

import java.util.Map;

/**
 * Guava function, transforms Map.Entry into {@link Pair} instance
 *
 * @author alexey
 * Date: 10/21/11
 */
public class MapEntryPairFunction<K, V> implements Function<Map.Entry<K,V>, Pair<K, V>> {
    /**
     * @param input map entry
     * @return pair
     */
    @Override
    public Pair<K, V> apply(Map.Entry<K, V> input) {
        return new Pair<K, V>(input.getKey(), input.getValue());
    }
}
