package ru.concerteza.util.keys;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;

/**
 * User: alexey
 * Date: 7/13/12
 */
public class KeyOperationsTest {
    private static final Iterable<Source> SOURCE = ImmutableList.of(
            new Source("a11", "0"),
            new Source("bar", "1"),
            new Source("baz", "2"),
            new Source("baz42", "3"),
            new Source("foo", "4"),
            new Source("zoo", "5")
    );

    private static final Iterable<Target> TARGET = ImmutableList.of(
            new Target("aaa", "40"),
            new Target("bar", "41"),
            new Target("bar", "42"),
            new Target("bar1", "43"),
            new Target("bar2", "44"),
            new Target("bar3", "45"),
            new Target("baz", "46"),
            new Target("baz1", "47"),
            new Target("baz2", "48"),
            new Target("foo", "49")
    );

    @Test
    public void testMerge() {
        Iterator<String> joined = KeyOperations.mergeJoin(SOURCE.iterator(), TARGET.iterator(), Joiner.INSTANCE);
        assertJoined(joined);
    }

    @Test
    public void testMergeLeft() {
        Iterator<String> joined = KeyOperations.mergeLeftJoin(SOURCE.iterator(), TARGET.iterator(), Joiner.INSTANCE);
        assertLeftJoined(joined);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSourceOrderFail() {
        Iterator<Source> source = ImmutableList.of(
                new Source("bar", "1"),
                new Source("a11", "0")).iterator();
        Iterator<Target> target = ImmutableList.of(
                new Target("boo", "40"),
                new Target("foo", "41")).iterator();
        fireTransform(KeyOperations.mergeJoin(source, target, Joiner.INSTANCE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTargetOrderFail() {
        Iterator<Source> source = ImmutableList.of(
                new Source("bar", "1"),
                new Source("foo", "0")).iterator();
        Iterator<Target> target = ImmutableList.of(
                new Target("foo", "40"),
                new Target("boo", "41")).iterator();
        fireTransform(KeyOperations.mergeJoin(source, target, Joiner.INSTANCE));
    }

    @Test
    public void testNestedLoop() {
        Iterator<String> joined = KeyOperations.nestedLoopJoin(SOURCE.iterator(), TARGET, Joiner.INSTANCE);
        assertJoined(joined);
    }

    @Test
    public void testNestedLoopLeft() {
        Iterator<String> joined = KeyOperations.nestedLoopLeftJoin(SOURCE.iterator(), TARGET, Joiner.INSTANCE);
        assertLeftJoined(joined);
    }

    @Test
    public void testHash() {
        Multimap<String, Target> map = Multimaps.index(TARGET, TargetKeyFun.INSTANCE);
        Iterator<String> joined = KeyOperations.hashJoin(SOURCE.iterator(), map, Joiner.INSTANCE);
        assertJoined(joined);
    }

    @Test
    public void testHashLeft() {
        Multimap<String, Target> map = Multimaps.index(TARGET, TargetKeyFun.INSTANCE);
        Iterator<String> joined = KeyOperations.hashLeftJoin(SOURCE.iterator(), map, Joiner.INSTANCE);
        assertLeftJoined(joined);
    }

    @Test
    public void testAggregate() {
        Iterator<Source> source = ImmutableList.of(
                new Source("foo", "42"),
                new Source("foo", "41"),
                new Source("bar", "43")).iterator();
        Collection<String> col = KeyOperations.groupByKey(source, Aggregator.INSTANCE);
        List<String> list = ImmutableList.copyOf(col);
        assertEquals("Size fail", 2, list.size());
        assertEquals("Data fail", "bar-43", list.get(0));
        assertEquals("Data fail", "foo-42-41", list.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAggregateOrderedIAE() {
        Iterator<Source> source = ImmutableList.of(
                new Source("foo", "42"),
                new Source("foo", "41"),
                new Source("bar", "43")).iterator();
        fireTransform(KeyOperations.groupOrderedByKey(source, Aggregator.INSTANCE));
    }

    @Test
    public void testAggregateOrdered() {
        Iterator<Source> source = ImmutableList.of(
                new Source("bar", "43"),
                new Source("foo", "42"),
                new Source("foo", "41")).iterator();
        Iterator<String> col = KeyOperations.groupOrderedByKey(source, Aggregator.INSTANCE);
        List<String> list = ImmutableList.copyOf(col);
        assertEquals("Size fail", 2, list.size());
        assertEquals("Data fail", "bar-43", list.get(0));
        assertEquals("Data fail", "foo-42-41", list.get(1));
    }

    private void assertJoined(Iterator<String> joined) {
        List<String> list = ImmutableList.copyOf(joined);
        assertEquals("Size fail", 4, list.size());
        assertEquals("Data fail", "bar-1,41", list.get(0));
        assertEquals("Data fail", "bar-1,42", list.get(1));
        assertEquals("Data fail", "baz-2,46", list.get(2));
        assertEquals("Data fail", "foo-4,49", list.get(3));
    }

    private void assertLeftJoined(Iterator<String> joined) {
        List<String> list = ImmutableList.copyOf(joined);
        assertEquals("Size fail", 7, list.size());
        assertEquals("Data fail", "a11-0,", list.get(0));
        assertEquals("Data fail", "bar-1,41", list.get(1));
        assertEquals("Data fail", "bar-1,42", list.get(2));
        assertEquals("Data fail", "baz-2,46", list.get(3));
        assertEquals("Data fail", "baz42-3,", list.get(4));
        assertEquals("Data fail", "foo-4,49", list.get(5));
        assertEquals("Data fail", "zoo-5,", list.get(6));
    }

    private static class Source implements KeyEntry {
        private final String key;
        private final String value;

        private Source(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public String toString() {
            return key + "-" + value;
        }
    }

    private static class Target implements KeyEntry {
        private final String key;
        private final String value;

        private Target(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public String toString() {
            return key + "-" + value;
        }
    }

    private enum Joiner implements KeyJoiner<Source, Target, String> {
        INSTANCE;
        @Override
        public String join(Source source, @Nullable Target target) {
            String tarval = null != target ? target.value : "";
            return source.key() + "-" + source.value + "," + tarval;
        }
    }

    private enum TargetKeyFun implements Function<Target, String> {
        INSTANCE;
        @Override
        public String apply(@Nullable Target input) {
            return input.key;
        }
    }

    private enum Aggregator implements KeyAggregator<Source, String> {
        INSTANCE;
        @Override
        public String aggregate(Source source, @Nullable String previous) {
            String prefix = null != previous ? previous : source.key;
            return prefix + "-" + source.value;
        }
    }
}
