package ru.concerteza.util.collection.limited;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Iterator, that stops iteration when inner iterator throw predefined error.
 * Thread-safe if target iterator is thread-safe.
 *
 * @author alexey
 * Date: 10/28/11
 * @see LimitedCatchErrorIteratorTest
 */
public class LimitedCatchErrorIterator<T> implements Iterator<T> {
    private final Iterator<T> target;
    private final Class<? extends Error> limitErrorClass;

    /**
     * Constructor. Consider using {@link LimitedCatchErrorIterator#of(java.util.Iterator, Class)} instead
     *
     * @param target target iterator
     * @param limitErrorClass error class to catch and stop iteration
     */
    public LimitedCatchErrorIterator(Iterator<T> target, Class<? extends Error> limitErrorClass) {
        checkNotNull(target);
        checkNotNull(limitErrorClass);
        this.target = target;
        this.limitErrorClass = limitErrorClass;
    }

    /**
     * Creates {@link LimitedCatchErrorIterator} instance
     *
     * @param target target iterator
     * @param limitErrorClass error class to catch and stop iteration
     * @param <T> target iterator generic parameter
     * @return {@link LimitedCatchErrorIterator} instance
     */
    public static <T> LimitedCatchErrorIterator<T> of(Iterator<T> target, Class<? extends Error> limitErrorClass) {
        return new LimitedCatchErrorIterator<T>(target, limitErrorClass);
    }

    /**
     * Static-import friendly alias for {@link #of(java.util.Iterator, Class)}
     *
     * @param target target iterator
     * @param limitErrorClass error class to catch and stop iteration
     * @param <T> target iterator generic parameter
     * @return {@link LimitedCatchErrorIterator} instance
     */
    public static <T> LimitedCatchErrorIterator<T> limitedCatchErrorIterator(Iterator<T> target, Class<? extends Error> limitErrorClass) {
        return new LimitedCatchErrorIterator<T>(target, limitErrorClass);
    }

    /**
     * @return target iterator {@code hasNext()} or {@code false} on expected Error
     */
    @Override
    public boolean hasNext() {
        try {
            return target.hasNext();
        } catch(Error e) {
            if(e.getClass().getName().equals(limitErrorClass.getName())) {
                return false;
            } else throw new RuntimeException(e);
        }
    }

    /**
     * @return next element
     */
    @Override
    public T next() {
        return target.next();
    }

    @Override
    public void remove() {
        target.remove();
    }
}
