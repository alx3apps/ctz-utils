package ru.concerteza.util.io.holder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 8/5/12
 */
public class PlainListHolderTest {
    @Test
    public void test() {
        Holder holder = new Holder();
        holder.postConstruct();
        assertEquals("Size fail", 2, holder.list.size());
        assertEquals("Data fail", "foo", holder.list.get(0));
        assertEquals("Data fail", "bar", holder.list.get(1));
    }

    private static class Holder extends PlainListHolder {
        @Override
        protected String valueRegex() {
            return "^\\s*(?<value>.+)\\s*$";
        }

        @Override
        protected String commentRegex() {
            return "^\\s*#.*$";
        }

        @Override
        protected String filePath() {
            return "classpath:/list.txt";
        }
    }
}
