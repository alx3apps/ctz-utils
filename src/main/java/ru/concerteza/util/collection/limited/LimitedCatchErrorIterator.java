package ru.concerteza.util.collection.limited;

import com.google.common.collect.AbstractIterator;
import org.apache.commons.lang.UnhandledException;

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
public class LimitedCatchErrorIterator<T> extends AbstractIterator<T> {
    private final Iterator<T> target;
    private final Class<? extends Error> limitErrorClass;

    /**
     * Protected constructor, use {@link LimitedCatchErrorIterator#of(java.util.Iterator, Class)} instead
     *
     * @param target target iterator
     * @param limitErrorClass error class to catch and stop iteration
     */
    protected LimitedCatchErrorIterator(Iterator<T> target, Class<? extends Error> limitErrorClass) {
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

    @Override
    protected T computeNext() {
        try {
            return target.hasNext() ? target.next() : endOfData();
        } catch (Error e) {
            if(e.getClass().getName().equals(limitErrorClass.getName())) {
                return endOfData();
            } else throw new UnhandledException(e);
        }
    }
}
