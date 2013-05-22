package ru.concerteza.util.collection.limited;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Iterator, that stops iteration after reaching predefined limit.
 * Thread-safe if target iterator is thread-safe.
 *
 * @author alexey
 * Date: 10/28/11
 * @see LimitedSizeIteratorTest
 */

public class LimitedSizeIterator<T> implements Iterator<T> {
    private final Iterator<T> target;
    private final long limit;
    private AtomicInteger counter = new AtomicInteger(0);

    /**
     * Constructor, consider using {@link LimitedSizeIterator#of(java.util.Iterator, long)} instead.
     *
     * @param target target iterator
     * @param limit size limit
     */
    public LimitedSizeIterator(Iterator<T> target, long limit) {
        checkNotNull(target);
        checkArgument(limit > 0, "Limit must be >= zero, was: %s", limit);
        this.target = target;
        this.limit = limit;
    }

    /**
     * Creates {@link LimitedSizeIterator} instance
     *
     * @param target target iterator
     * @param limit size limit
     * @param <T> target iterator generic parameter
     * @return {@link LimitedSizeIterator} instance
     */
    public static <T> LimitedSizeIterator<T> of(Iterator<T> target, long limit) {
        return new LimitedSizeIterator<T>(target, limit);
    }

    /**
     * Static-import friendly alias for {@link #of(java.util.Iterator, long)}
     *
     * @param target target iterator
     * @param limit size limit
     * @param <T> target iterator generic parameter
     * @return {@link LimitedSizeIterator} instance
     */
    public static <T> LimitedSizeIterator<T> limitedSizeIterator(Iterator<T> target, long limit) {
        return new LimitedSizeIterator<T>(target, limit);
    }

    /**
     * @return target iterator {@code hasNext()} or {@code false} on threshold exceed
     */
    @Override
    public boolean hasNext() {
        return counter.get() < limit && target.hasNext();
    }

    /**
     * @return next element
     */
    @Override
    public T next() {
        counter.incrementAndGet();
        return target.next();
    }

    @Override
    public void remove() {
        target.remove();
    }
}
