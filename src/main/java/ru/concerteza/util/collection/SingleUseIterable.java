package ru.concerteza.util.collection;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Iterable, allows only one call of <code>iterator()</code> method.
 * May be useful for 'foreach' operations over iterators.
 * Thread-safe if target iterator is thread-safe.
 *
 * @author alexey
 * Date: 11/19/11
 * @see SingleUseIterableTest
 */
public class SingleUseIterable<T> implements Iterable<T> {
    private final Iterator<T> iter;
    private final AtomicBoolean notUsed = new AtomicBoolean(true);

    /**
     * Constructor, consider using {@link SingleUseIterable#of(java.util.Iterator)} instead
     *
     * @param iter iterator to wrap
     */
    public SingleUseIterable(Iterator<T> iter) {
        this.iter = iter;
    }

    /**
     * Generic-friendly factory method
     *
     * @param iter iterator to wrap
     * @param <T> iterator type
     * @return SingleUseIterable instance
     */
    public static <T> Iterable<T> of(Iterator<T> iter) {
        checkNotNull(iter);
        return new SingleUseIterable<T>(iter);
    }

    /**
     * Generic-friendly factory method, alias for {@link #of(java.util.Iterator)}
     *
     * @param iter iterator to wrap
     * @param <T> iterator type
     * @return SingleUseIterable instance
     */
    public static <T> Iterable<T> singleUseIterable(Iterator<T> iter) {
        return of(iter);
    }

    /**
     * @return provided iterator
     * @throws IllegalStateException on more than one usage attempt
     */
    @Override
    public Iterator<T> iterator() {
        boolean nu = notUsed.getAndSet(false);
        checkState(nu, "SingleUseIterable is already used: %s", this);
        return this.iter;
    }
}
