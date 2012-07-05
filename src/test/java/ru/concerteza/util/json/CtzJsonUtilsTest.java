package ru.concerteza.util.json;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.concerteza.util.json.CtzJsonUtils;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: alexey
 * Date: 11/19/11
 */
public class CtzJsonUtilsTest {
    @Test
    public void testParseMap() {
        Resource resource = new ClassPathResource("/CtzJsonUtilsTest.json");
        Map<String, String> map = CtzJsonUtils.parseStringMap(resource);
        assertTrue(map.containsKey("foo"));
        assertTrue(map.containsKey("baz"));
        assertEquals("bar", map.get("foo"));
        assertEquals("42", map.get("baz"));
    }
}
