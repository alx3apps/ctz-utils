package ru.concerteza.util.collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static ru.concerteza.util.collection.CtzCollectionUtils.keySetToMap;

/**
 * User: alexey
 * Date: 8/3/12
 */
public class CtzCollectionUtilsTest {
    @Test
    public void testKeySetToMap() {
        Set<String> set = ImmutableSet.of("foo", "bar1", "baz42");
        Map<String, Integer> map = keySetToMap(set, ValueFun.INSTANCE);
        assertEquals("Size fail", 3, map.size());
        assertTrue("Key fail fail", map.containsKey("foo"));
        assertTrue("Key fail fail", map.containsKey("bar1"));
        assertTrue("Key fail fail", map.containsKey("baz42"));
        assertEquals("Value fail", 3, (int) map.get("foo"));
        assertEquals("Value fail", 4, (int) map.get("bar1"));
        assertEquals("Value fail", 5, (int) map.get("baz42"));
    }

    private enum ValueFun implements Function<String, Integer> {
        INSTANCE;
        @Override
        public Integer apply(@Nullable String input) {
            return input.length();
        }
    }
}
