package ru.concerteza.util.collection;

import com.google.common.collect.AbstractIterator;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.concurrent.BlockingQueue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Iterator implementation over blocking queue, waits for data from the queue
 * until receiving predefined end-of-data object.
 *
 * @author alexkasko
 * Date: 8/31/13
 */
public class EndOfDataBlockingQueueIterator<T> extends AbstractIterator<T> {

    private final BlockingQueue<T> delegate;
    private final T eod;
    private final T error;

    /**
     * Constructor
     *
     * @param delegate blocking queue
     * @param eod end-of-data object
     * @param error error object
     */
    public EndOfDataBlockingQueueIterator(BlockingQueue<T> delegate, T eod, T error) {
        checkNotNull(delegate, "Provided blocking queue is null");
        checkNotNull(eod, "Provided end-of-data object is null");
        this.delegate = delegate;
        this.eod = eod;
        this.error = error;
    }

    /**
     * Generic-friendly factory method
     *
     * @param delegate blocking queue
     * @param endOfData end-of-data object
     * @param <T> queue generic type
     * @return iterator instance
     */
    public static <T> EndOfDataBlockingQueueIterator<T> endOfDataBlockingQueueIterator(BlockingQueue<T> delegate, T endOfData, T error) {
        return new EndOfDataBlockingQueueIterator<T>(delegate, endOfData, error);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected T computeNext() {
        try {
            T cur = delegate.take();
            checkNotNull(cur, "Null element taken from blocking queue");
            if(error == cur) throw new CtzCollectionException("Error object received, cancelling processing");
            if(eod == cur) return endOfData();
            return cur;
        } catch (InterruptedException e) {
            throw new CtzCollectionException("Blocking queue take operation interrupted", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("eod", eod).
                append("error", error).
                toString();
    }
}
