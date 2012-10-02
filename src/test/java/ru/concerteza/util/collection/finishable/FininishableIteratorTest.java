package ru.concerteza.util.collection.finishable;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 10/2/12
 */
public class FininishableIteratorTest {
    public void test() {
        Iterator<String> source = ImmutableList.of("foo", "bar").iterator();
        FiFu ff = new FiFu();
        Iterator<String> res = FinishableIterator.of(source, ff);
        List<String> list = ImmutableList.copyOf(res);
        assertEquals("Size fail", 2, list.size());
        assertEquals("Data fail", "foo", list.get(0));
        assertEquals("Data fail", "bar", list.get(1));
        assertEquals("Finishable fail", 42, ff.value);
    }

    private static class FiFu implements FinishableFunction<String, String> {
        private int value;

        @Override
        public void finish() {
            value += 40;
        }

        @Override
        public String apply(@Nullable String input) {
            value += 1;
            return input;
        }
    }
}
