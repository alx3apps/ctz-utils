package ru.concerteza.util.collection;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Iterator wrapper, counts iterated elements. Thread-safe if target iterator is thread-safe.
 *
 * @author alexey
 * Date: 7/8/12
 * @see CountingIteratorTest
 */
public class CountingIterator<T> implements Iterator<T> {
    private final Iterator<T> target;
    private final AtomicLong count = new AtomicLong(0);

    /**
     * @param target target iterator
     */
    public CountingIterator(Iterator<T> target) {
        checkNotNull(target, "Provided iterator is null");
        this.target = target;
    }

    /**
     * Generic-friendly factory method
     *
     * @param iterator target iterator
     * @param <T> element type
     * @return instance
     */
    public static <T> CountingIterator<T> of(Iterator<T> iterator) {
        return new CountingIterator<T>(iterator);
    }

    /**
     * Static-import friendly alias for {@link #of(java.util.Iterator)}
     *
     * @param iterator target iterator
     * @param <T> element type
     * @return instance
     */
    public static <T> CountingIterator<T> countingIterator(Iterator<T> iterator) {
        return new CountingIterator<T>(iterator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return target.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T next() {
        T res = target.next();
        count.incrementAndGet();
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        target.remove();
    }

    /**
     * Count accessor
     *
     * @return current iterated elements count
     */
    public long getCount() {
        return count.get();
    }

    /**
     * Integer count accessor
     *
     * @return current iterated elements count, {@code -1} on overflow
     */
    public int getCountInt() {
        long res = count.get();
        if (res > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) res;
    }
}
