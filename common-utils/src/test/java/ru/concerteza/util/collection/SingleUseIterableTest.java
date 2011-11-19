package ru.concerteza.util.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * User: alexey
 * Date: 11/19/11
 */
public class SingleUseIterableTest {

    @Test(expected = IllegalStateException.class)
    public void testSingle() {
        Iterable<String> iter = SingleUseIterable.wrap(ImmutableList.of("foo", "bar", "baz").iterator());
        iter.iterator();
        iter.iterator();
    }

    @Test
    public void testForEach() {
        Iterable<String> iter = SingleUseIterable.wrap(ImmutableList.of("foo", "bar", "baz").iterator());
        List<String> list = Lists.newArrayList();
        for(String str : iter) {
            list.add(str);
        }
        Assert.assertEquals("foo", list.get(0));
        Assert.assertEquals("bar", list.get(1));
        Assert.assertEquals("baz", list.get(2));
    }
}
