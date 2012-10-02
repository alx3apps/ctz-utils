package ru.concerteza.util.collection.finishable;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Iterator wrapper for using with {@link FinishableFunction}, calls {@link ru.concerteza.util.collection.finishable.FinishableFunction#finish()}
 * on iterator exhaustion. Thread-safe is source iterator is thread-safe.
 *
 * @author alexey
 * Date: 10/2/12
 * @see FinishableFunction
 * @see FininishableIteratorTest
 */
public class FinishableIterator<F, T> implements Iterator<T> {
    private final Iterator<F> fromIter;
    private final FinishableFunction<? super F, ? extends T> function;
    private AtomicBoolean finished = new AtomicBoolean(false);

    /**
     * @param fromIter source iterator
     * @param function finishable function
     */
    public FinishableIterator(Iterator<F> fromIter, FinishableFunction<? super F, ? extends T> function) {
        checkNotNull(fromIter, "Provided iterator is null");
        checkNotNull(function, "Provided function is null");
        this.fromIter = fromIter;
        this.function = function;
    }

    public static <F, T> FinishableIterator<F, T> of(Iterator<F> fromIter, FinishableFunction<? super F, ? extends T> function) {
        return new FinishableIterator<F, T>(fromIter, function);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        boolean res = fromIter.hasNext();
        // todo: check thread safety
        if(!res && !finished.getAndSet(true)) {
            function.finish();
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T next() {
        F from = fromIter.next();
        return function.apply(from);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        fromIter.remove();
    }
}
