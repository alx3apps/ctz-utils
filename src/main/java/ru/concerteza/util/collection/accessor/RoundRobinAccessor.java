package ru.concerteza.util.collection.accessor;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round robin collection accessor. Copies provided collection into inner immutable one on creation. Thread-safe.
 * See usage example in {@link RoundRobinAccessorTest}
 *
 * @author  alexey
 * Date: 6/11/12
 * @see Accessor
 */
@Deprecated // use com.alexkasko.springjdbc.parallel.accessor
public class RoundRobinAccessor<T> extends AbstractAccessor<T> {
    private AtomicInteger index = new AtomicInteger(0);

    /**
     * @param target collection to wrap
     * @param <T> target collection generic parameter
     * @return {@link RoundRobinAccessor} over provided collection
     */
    public static <T> RoundRobinAccessor<T> of(Collection<T> target) {
        return new RoundRobinAccessor<T>(target);
    }

    /**
     * Protected constructors
     *
     * @param target collection to wrap
     */
    protected RoundRobinAccessor(Collection<T> target) {
        super(target);
    }

    /**
     * @return next collection element
     */
    public T get() {
        return target.get(incrementAndGet());
    }

    /**
     * Atomically increments index using target list size modulus
     * @return incremented index
     */
    private int incrementAndGet() {
        for (;;) {
            int current = index.get();
            int next = (current < target.size() - 1) ? current + 1 : 0;
            if (index.compareAndSet(current, next)) return current;
        }
    }
}
