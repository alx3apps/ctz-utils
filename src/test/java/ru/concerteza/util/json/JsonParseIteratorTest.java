package ru.concerteza.util.json;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.junit.Test;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static ru.concerteza.util.json.JsonParseIterator.jsonParseIterator;

/**
 * User: alexkasko
 * Date: 7/12/13
 */
public class JsonParseIteratorTest {
    @Test
    public void test() {
        String json = "['foo', 'bar', 'baz']";
        Iterator<String> iter = jsonParseIterator(new Gson(), new StringReader(json), String.class);
        List<String> list = ImmutableList.copyOf(iter);
        assertEquals("Size fail", 3, list.size());
        assertEquals("Data fail", "foo", list.get(0));
        assertEquals("Data fail", "bar", list.get(1));
        assertEquals("Data fail", "baz", list.get(2));
    }
}
