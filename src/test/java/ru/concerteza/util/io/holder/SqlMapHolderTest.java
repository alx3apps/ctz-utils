package ru.concerteza.util.io.holder;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexey
 * Date: 8/5/12
 */
public class SqlMapHolderTest {
    @Test
    public void test() {
        Holder holder = new Holder();
        holder.postConstruct();
        assertEquals("Data fail", "select foo from bar where 41 > 42", holder.get("foo-query"));
        assertEquals("Data fail", "select bar from foo where 42 < 41", holder.get("bar-query"));
    }

    private class Holder extends SqlMapHolder {
        @Override
        protected String sqlFilePath() {
            return "classpath:/map.sql";
        }
    }
}
