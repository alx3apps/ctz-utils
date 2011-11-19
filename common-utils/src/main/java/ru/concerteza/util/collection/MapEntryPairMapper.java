package ru.concerteza.util.collection;

import com.google.common.base.Function;
import ru.concerteza.util.value.Pair;

import java.util.Map;

/**
 * User: alexey
 * Date: 10/21/11
 */
public class MapEntryPairMapper<K, V> implements Function<Map.Entry<K,V>, Pair<K, V>> {
    @Override
    public Pair<K, V> apply(Map.Entry<K, V> input) {
        return new Pair<K, V>(input.getKey(), input.getValue());
    }
}
