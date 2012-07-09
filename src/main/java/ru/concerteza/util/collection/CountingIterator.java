package ru.concerteza.util.collection;

import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/8/12
 */
public class CountingIterator<T> implements Iterator<T> {
    private final Iterator<T> target;
    private AtomicLong count = new AtomicLong(0);

    public CountingIterator(Iterator<T> target) {
        checkNotNull(target, "Provided iterator is null");
        this.target = target;
    }

    public static <T> CountingIterator<T> of(Iterator<T> iterator) {
        return new CountingIterator<T>(iterator);
    }

    @Override
    public boolean hasNext() {
        return target.hasNext();
    }

    @Override
    public T next() {
        T res = target.next();
        count.incrementAndGet();
        return res;
    }

    @Override
    public void remove() {
        target.remove();
    }

    public long getCount() {
        return count.get();
    }
}
