package ru.concerteza.util.collection.maps;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: alexey
 * Date: 8/26/12
 */
public class IgnoreNullImmutableMapBuilderTest {
    @Test
    public void test() {
        Map<String, String> map = IgnoreNullImmutableMapBuilder.<String, String>builder()
                .put("foo", "42")
                .put("bar", null)
                .build();
        assertEquals("Size fail", 1, map.size());
        assertTrue("Key fail", map.containsKey("foo"));
        assertEquals("Value fail", "42", map.get("foo"));
    }
}
