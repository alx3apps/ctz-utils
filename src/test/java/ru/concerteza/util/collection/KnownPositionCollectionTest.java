package ru.concerteza.util.collection;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import ru.concerteza.util.collection.KnownPositionCollection;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: alexey
 * Date: 8/11/11
 */
public class KnownPositionCollectionTest {

    @Test
    public void test() {
        List<String> list = ImmutableList.of("foo", "bar", "baz");
        KnownPositionCollection<String> col = KnownPositionCollection.wrap(list);
        int count = 0;
        for (String val : col) {
            assertEquals("Position fail", count, col.position());
            if (col.isFirstPosition()) {
                assertEquals("First el fail", "foo", val);
                assertTrue(col.isNotLastPosition());
            } else if (col.isLastPosition()) {
                assertEquals("Last el fail", "baz", val);
                assertFalse(col.isNotLastPosition());
            } else {
                assertEquals("Middle el fail", "bar", val);
                assertTrue(col.isNotLastPosition());
            }
            count += 1;
        }
        //test reuse
        int countReuse = 0;
        for (String val : col) {
            assertEquals("Position reuse fail", countReuse, col.position());
            if (col.isFirstPosition()) {
                assertEquals("First el reuse fail", "foo", val);
                assertTrue(col.isNotLastPosition());
            } else if (col.isLastPosition()) {
                assertEquals("Last el reuse fail", "baz", val);
                assertFalse(col.isNotLastPosition());
            } else {
                assertEquals("Middle el reuse fail", "bar", val);
                assertTrue(col.isNotLastPosition());
            }
            countReuse += 1;
        }
    }
}
