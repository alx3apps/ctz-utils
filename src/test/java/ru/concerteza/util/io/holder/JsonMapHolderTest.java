package ru.concerteza.util.io.holder;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexey
 * Date: 8/5/12
 */
public class JsonMapHolderTest {
    @Test
    public void test() {
        Holder holder = new Holder();
        holder.postConstruct();
        assertEquals("Data fail", "41", holder.get("foo"));
        assertEquals("Data fail", "42", holder.get("bar"));
    }

    private static class Holder extends JsonMapHolder {
        @Override
        protected String jsonFilePath() {
            return "classpath:/map.json";
        }
    }
}
