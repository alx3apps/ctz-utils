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

public class LimitedSizeIterator<T> extends AbstractIterator<T> {
    private final Iterator<T> target;
    private final int limit;
    private AtomicInteger counter = new AtomicInteger(-1);

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
    protected T computeNext() {
        if(counter.incrementAndGet() < limit && target.hasNext()) {
            return target.next();
        } else return endOfData();
    }
}
