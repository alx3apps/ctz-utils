package ru.concerteza.util.io;

import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexey
 * Date: 7/7/12
 */
public class SqlListParserTest {
    @Test
    public void test() throws Exception {
        Map<String, String> queries = SqlListParser.parseToMap("/queries_list.sql");
        assertEquals("Size fail", 2, queries.size());
        assertTrue("Key fail", queries.containsKey("foo-query"));
        assertTrue("Key fail", queries.containsKey("bar-query"));
        assertEquals("Value fail", "select foo from bar where baz=41 and boo=42", queries.get("foo-query"));
        assertEquals("Value fail", "select bar from foo where baz=41 and boo=42", queries.get("bar-query"));
    }
}
