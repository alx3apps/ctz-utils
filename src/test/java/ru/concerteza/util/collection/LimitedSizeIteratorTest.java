package ru.concerteza.util.collection;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 10/28/11
 */
public class LimitedSizeIteratorTest {
    @Test
    public void testLimit() {
        List<String> list = ImmutableList.of("foo", "bar", "baz", "42");
        Iterator<String> iter = LimitedSizeIterator.wrap(list.iterator(), 3);
        List<String> copied = ImmutableList.copyOf(iter);
        assertEquals("Limit fail", 3, copied.size());
    }
}
