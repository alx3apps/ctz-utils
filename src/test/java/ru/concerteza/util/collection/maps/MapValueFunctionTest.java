package ru.concerteza.util.collection.maps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static ru.concerteza.util.collection.maps.MapValueFunction.mapValueFunction;

/**
 * User: alexkasko
 * Date: 8/31/13
 */
public class MapValueFunctionTest {

    @Test
    public void test() {
        Map<Integer, String> map = ImmutableMap.of(1, "foo", 2, "bar");
        List<String> list = ImmutableList.copyOf(Iterables.transform(map.entrySet(), mapValueFunction(Integer.class, String.class)));
        assertEquals("Size fail", 2, list.size());
        assertEquals("Data fail", "foo", list.get(0));
        assertEquals("Data fail", "bar", list.get(1));
    }
}
