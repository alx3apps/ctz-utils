package ru.concerteza.util.concurrency;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reference holder, hold only first setted value, ignore subsequent values
 *
 * @author alexey
 * Date: 6/12/12
 */
public class FirstValueHolder<T> {
    private AtomicReference<T> target = new AtomicReference<T>();

    /**
     * @return holded value or null if no value set
     */
    public T get() {
        return target.get();
    }

    /**
     * @param target value to set, ignored if value was already set
     */
    public void set(T target) {
        checkNotNull(target, "Holded value must be non null");
        this.target.compareAndSet(null, target);
    }
}
