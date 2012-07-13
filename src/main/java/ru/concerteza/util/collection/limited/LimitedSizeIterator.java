package ru.concerteza.util.collection.limited;

import com.google.common.collect.AbstractIterator;

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
    private final int limit;
    private AtomicInteger counter = new AtomicInteger(0);

    protected LimitedSizeIterator(Iterator<T> target, int limit) {
        checkNotNull(target);
        checkArgument(limit > 0, "Limit must be >= zero, was: %s", limit);
        this.target = target;
        this.limit = limit;
    }

    public static <T> LimitedSizeIterator<T> of(Iterator<T> target, int limit) {
        return new LimitedSizeIterator<T>(target, limit);
    }

    @Override
    public boolean hasNext() {
        return counter.get() < limit && target.hasNext();
    }

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
