package ru.concerteza.util.concurrency;

import com.google.common.util.concurrent.Callables;
import org.junit.Test;
import ru.concerteza.util.concurrency.CallableList;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 5/5/12
 */
public class CallableListTest {
    @Test
    public void test() throws Exception {
        CallableList<String> list = new CallableList<String>()
                .add(Callables.returning("foo"))
                .add(Callables.returning("bar"))
                .add(Callables.returning("baz"));
        List<String> res = list.call();
        assertEquals("Size fail", 3, res.size());
        assertEquals("foo", res.get(0));
        assertEquals("bar", res.get(1));
        assertEquals("baz", res.get(2));
    }
}
