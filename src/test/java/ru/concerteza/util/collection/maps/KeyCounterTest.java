package ru.concerteza.util.collection.maps;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexey
 * Date: 8/26/12
 */
public class KeyCounterTest {
    @Test
    public void test() {
        KeyCounter counter = new KeyCounter();
        counter.increment("foo");
        counter.increment("foo");
        counter.increment("bar", 42);
        assertEquals(2, counter.get("foo"));
        assertEquals(42, counter.get("bar"));
        assertEquals(0, counter.get("baz"));
    }
}
