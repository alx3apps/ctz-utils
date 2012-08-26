package ru.concerteza.util.collection.maps;

import com.google.common.collect.ForwardingMap;
import org.apache.commons.lang.mutable.MutableInt;

import java.util.HashMap;
import java.util.Map;

/**
 * 'String' -> 'counter' map implementation. May substitute Map<String, Integer> maps
 * that requires to increment the value. Inspired by <a href="http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java">this SO question</a>.
 * Not thread-safe.
 *
 * @author alexey
 * Date: 8/26/12
 * @see KeyCounterTest
 */
public class KeyCounter {
    private final Map<String, MutableInt> delegate = new HashMap<String, MutableInt>();

    /**
     * Increments counter for given key by one
     *
     * @param key key
     * @return incremented value
     */
    public int increment(String key) {
        return increment(key, 1);
    }

    /**
     * Increments counter for given key by given value
     *
     * @param key key
     * @param value value to add to counter
     * @return incremented value
     */
    public int increment(String key, int value) {
        MutableInt existed = delegate.get(key);
        final int res;
        if(null != existed) {
            existed.add(value);
            res = existed.intValue();
        } else {
            delegate.put(key, new MutableInt(value));
            res = value;
        }
        return res;
    }

    /**
     * @param key key
     * @return counter value for given key, 0 for unknown key
     */
    public int get(String key) {
        MutableInt existed = delegate.get(key);
        return null != existed ? existed.intValue() : 0;
    }
}
