package ru.concerteza.util.collection.join;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;
import ru.concerteza.util.value.Pair;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static junit.framework.Assert.assertEquals;
import static ru.concerteza.util.CtzFormatUtils.format;
import static ru.concerteza.util.collection.CtzCollectionUtils.fireTransform;

/**
 * User: alexey
 * Date: 7/4/12
 */
public class JoinedIteratorTest {
    @Test
    public void test() {
        Iterator<CsvEntry> source = ImmutableList.of(
                new CsvEntry("a11|0"),
                new CsvEntry("bar|1"),
                new CsvEntry("baz|2"),
                new CsvEntry("baz42|3"),
                new CsvEntry("foo|4"),
                new CsvEntry("baza|5")
        ).iterator();
        Iterator<CsvEntry> target = ImmutableList.of(
                new CsvEntry("aaa|40"),
                new CsvEntry("bar|41"),
                new CsvEntry("bar|43"),
                new CsvEntry("bar1|42"),
                new CsvEntry("bar2|44"),
                new CsvEntry("bar3|45"),
                new CsvEntry("baz|47"),
                new CsvEntry("baz1|46"),
                new CsvEntry("baz2|48"),
                new CsvEntry("foo|49")
        ).iterator();
        Iterator<Pair<CsvEntry, CsvEntry>> res = JoinedIterator.of(source, target, new TestJoiner());
        List<Pair<CsvEntry, CsvEntry>> list = ImmutableList.copyOf(res);
        assertEquals("Size fail", 4, list.size());
        assertEquals("Data fail", "bar|1|41", dump(list.get(0)));
        assertEquals("Data fail", "bar|1|43", dump(list.get(1)));
        assertEquals("Data fail", "baz|2|47", dump(list.get(2)));
        assertEquals("Data fail", "foo|4|49", dump(list.get(3)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSourceOrderFail() {
        Iterator<CsvEntry> source = ImmutableList.of(
                new CsvEntry("bar|1"),
                new CsvEntry("a11|0")
        ).iterator();
        Iterator<CsvEntry> target = ImmutableList.of(
                new CsvEntry("boo|40"),
                new CsvEntry("foo|41")
        ).iterator();
        Iterator<Pair<CsvEntry, CsvEntry>> res = JoinedIterator.of(source, target, new TestJoiner());
        fireTransform(res);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTargetOrderFail() {
        Iterator<CsvEntry> source = ImmutableList.of(
                new CsvEntry("bar|1"),
                new CsvEntry("foo|0")
        ).iterator();
        Iterator<CsvEntry> target = ImmutableList.of(
                new CsvEntry("foo|40"),
                new CsvEntry("boo|41")
        ).iterator();
        Iterator<Pair<CsvEntry, CsvEntry>> res = JoinedIterator.of(source, target, new TestJoiner());
        fireTransform(res);
    }

    private String dump(Pair<CsvEntry, CsvEntry> en) {
        String firstKey = en.getFirst().getKey();
        String secondKey = en.getSecond().getKey();
        checkArgument(firstKey.equals(secondKey), "Result keys fail, was: '%s' and '%s'", firstKey, secondKey);
        return format("{}|{}|{}", firstKey, en.getFirst().getPayload(), en.getSecond().getPayload());
    }

    private class CsvEntry implements Comparable<CsvEntry> {
        private final String key;
        private final String payload;

        private CsvEntry(String line) {
            List<String> parts = ImmutableList.copyOf(Splitter.on("|").split(line));
            this.key = parts.get(0);
            this.payload = parts.get(1);
        }

        public String getKey() {
            return key;
        }

        public String getPayload() {
            return payload;
        }

        @Override
        public int compareTo(CsvEntry o) {
            return key.compareTo(o.key);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                    append("key", key).
                    append("payload", payload).
                    toString();
        }
    }

    private class TestJoiner implements Joiner<CsvEntry, CsvEntry, Pair<CsvEntry, CsvEntry>> {
        @Override
        public Pair<CsvEntry, CsvEntry> join(CsvEntry source, CsvEntry target) {
            return Pair.of(source, target);
        }
    }
}
