package ru.concerteza.util.collection.limited;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import ru.concerteza.util.collection.limited.LimitedCatchErrorIterator;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 10/28/11
 */
public class LimitedCatchErrorIteratorTest {
    private static final ThreeError THREE_ERROR = new ThreeError();

    @Test
    public void test() {
        Iterator<String> throwingIter = new ThreeIter();
        Iterator<String> limited = LimitedCatchErrorIterator.of(throwingIter, ThreeError.class);
        List<String> list = ImmutableList.copyOf(limited);
        assertEquals("Size fail", 3, list.size());
    }

    private class ThreeIter extends AbstractIterator<String> {
        int counter = 0;

        @Override
        protected String computeNext() {
            if (counter >= 3) throw THREE_ERROR;
            counter += 1;
            return RandomStringUtils.randomAlphanumeric(42);
        }
    }

    private static class ThreeError extends Error { }
}
