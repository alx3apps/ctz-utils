package ru.concerteza.util.collection;

import com.google.common.util.concurrent.ForwardingBlockingQueue;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.System.arraycopy;

/**
 * 1-to-1 data pipe on top of synchronous queue. Won't work for multiple producers/consumers.
 *
 * @author alexkasko
 * Date: 8/31/13
 */
public class DataPipeBlockingQueue extends ForwardingBlockingQueue<byte[]> {

    private final SynchronousQueue<byte[]> delegate = new SynchronousQueue<byte[]>();
    private final byte[] buffer1;
    private final byte[] buffer2;
    // deliberately non-atomic, used only from producer thread
    private boolean first = true;

    /**
     * Constructor
     *
     * @param maxSize max size of the data packet
     */
    public DataPipeBlockingQueue(int maxSize) {
        this.buffer1 = new byte[maxSize];
        this.buffer2 = new byte[maxSize];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BlockingQueue<byte[]> delegate() {
        return delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(byte[] bytes) throws InterruptedException {
        if (0 == bytes.length) {
            super.put(bytes);
        } else {
            byte[] buf = first ? buffer1 : buffer2;
            first = !first;
            arraycopy(bytes, 0, buf, 0, buf.length);
            super.put(buf);
        }
    }

    @Override
    public boolean offer(byte[] bytes, long timeout, TimeUnit unit) throws InterruptedException {
        if (0 == bytes.length) {
            return super.offer(bytes, timeout, unit);
        } else {
            byte[] buf = first ? buffer1 : buffer2;
            first = !first;
            arraycopy(bytes, 0, buf, 0, buf.length);
            super.put(buf);
        }
        return super.offer(bytes, timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("delegate", delegate).
                append("buffer1", buffer1).
                append("buffer2", buffer2).
                append("first", first).
                toString();
    }
}
