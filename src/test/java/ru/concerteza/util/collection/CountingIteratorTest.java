package ru.concerteza.util.collection;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexey
 * Date: 8/3/12
 */
public class CountingIteratorTest {

    @Test
    public void test() {
        CountingIterator<String> iter = CountingIterator.of(ImmutableList.of("foo", "bar", "baz").iterator());
        CtzCollectionUtils.fireTransform(iter);
        assertEquals("Count fail", 3, iter.getCount());
    }
}
