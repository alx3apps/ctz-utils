package ru.concerteza.util.json;

import com.google.gson.JsonParser;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.concerteza.util.json.CtzJsonUtils;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.concerteza.util.json.CtzJsonUtils.wrapJson;

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

    @Test
    public void testWrapJson() {
        assertEquals("Null fail", null, wrapJson(new JsonParser().parse("null")));
        assertEquals("Boolean fail", true, wrapJson(new JsonParser().parse("true")));
        assertEquals("Integer fail", 42, CtzJsonUtils.<Number>wrapJson(new JsonParser().parse("42")).intValue());
        assertEquals("String fail", "foo", wrapJson(new JsonParser().parse("'foo'")));
        Map<String, ?> map = wrapJson(new JsonParser().parse("{'foo':'41', 'bar': '42'}"));
        assertEquals("Size fail", 2, map.size());
        assertEquals("Data fail", "41", map.get("foo"));
        assertEquals("Data fail", "42", map.get("bar"));
    }
}
