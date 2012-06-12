package ru.concerteza.util.collection.accessor;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 6/11/12
 */
public class RoundRobinAccessorTest {

    @Test
    public void test() {
        RoundRobinAccessor accessor = RoundRobinAccessor.of(ImmutableList.of("foo", "bar", "baz"));
        assertEquals("foo", accessor.get());
        assertEquals("bar", accessor.get());
        assertEquals("baz", accessor.get());
        assertEquals("foo", accessor.get());
        assertEquals("bar", accessor.get());
        assertEquals("baz", accessor.get());
        assertEquals("foo", accessor.get());
    }
}
