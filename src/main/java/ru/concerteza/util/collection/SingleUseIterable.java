package ru.concerteza.util.collection;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Iterable, that allows only one call of <code>iterator()</code> method.
 * May be useful for 'foreach' operations over iterators.
 * Thread-safe if target iterator is thread-safe.
 *
 * @author alexey
 * Date: 11/19/11
 * @see SingleUseIterableTest
 */
public abstract class SingleUseIterable<T> implements Iterable<T> {
    private AtomicBoolean notUsed = new AtomicBoolean(true);

    @Override
    public Iterator<T> iterator() {
        boolean nu = notUsed.getAndSet(false);
        checkState(nu, "SingleUseIterable is already used: %s", this);
        return singleUseIterator();
    }

    protected abstract Iterator<T> singleUseIterator();

    public static <T> Iterable<T> of(Iterator<T> iter) {
        checkNotNull(iter);
        return new Wrapper<T>(iter);
    }

    private static class Wrapper<T> extends SingleUseIterable<T> {
        private final Iterator<T> iter;

        private Wrapper(Iterator<T> iter) {
            this.iter = iter;
        }

        @Override
        protected Iterator<T> singleUseIterator() {
            return iter;
        }
    }
}
