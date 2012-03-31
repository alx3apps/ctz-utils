package ru.concerteza.util.collection;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: alexey
 * Date: 10/28/11
 */
public class IgnoreNullImmutableMapBuilderTest {
    @Test
    public void testSize() {
        Map<String, String> built = new IgnoreNullImmutableMapBuilder<String, String>()
            .put("one", "foo")
            .put("two", null)
            .put("three", "bar")
            .build();
        assertEquals("Size fail", 2, built.size());
        assertNull(built.get("foo"));
    }
}
