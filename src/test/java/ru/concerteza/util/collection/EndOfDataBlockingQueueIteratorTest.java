package ru.concerteza.util.collection;

import org.junit.Test;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static ru.concerteza.util.collection.EndOfDataBlockingQueueIterator.endOfDataBlockingQueueIterator;

/**
 * User: alexkasko
 * Date: 8/31/13
 */
public class EndOfDataBlockingQueueIteratorTest {

    @Test
    public void test() throws InterruptedException {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(3);
        String eod = "eod";
        String error = "error";
        final Iterator<String> it = endOfDataBlockingQueueIterator(queue, eod, error);
        queue.put("foo");
        final AtomicReference<String> res = new AtomicReference<String>();
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                sb.append(it.next());
                sb.append(it.next());
                res.set(sb.toString());
            }
        });
        consumer.setDaemon(true);
        consumer.start();
        Thread.sleep(100);
        queue.put("bar");
        queue.put(eod);
        consumer.join();

        assertNotNull(res.get());
        assertEquals("foobar", res.get());
    }
}
