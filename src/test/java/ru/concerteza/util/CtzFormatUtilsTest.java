package ru.concerteza.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: alexey
 * Date: 4/27/11
 */
public class CtzFormatUtilsTest {
    @Test
    public void test() {
        final String res = CtzFormatUtils.format("foo{}{}", "bar", "baz");
        assertEquals("format fail", "foobarbaz", res);
    }
}
