package ru.concerteza.util.collection.maps;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: alexey
 * Date: 8/26/12
 */
public class LowerKeysImmutableMapBuilderTest {
    @Test
    public void test() {
        Map<String, String> map = LowerKeysImmutableMapBuilder.<String>builder()
                .put("fOO", "41")
                .put("BaR", "42")
                .build();
        assertEquals("Size fail", 2, map.size());
        assertTrue("Key fail", map.containsKey("foo"));
        assertTrue("Key fail", map.containsKey("bar"));
    }
}
