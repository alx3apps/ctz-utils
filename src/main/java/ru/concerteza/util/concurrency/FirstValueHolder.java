package ru.concerteza.util.concurrency;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 6/12/12
 */
public class FirstValueHolder<T> {
    private AtomicReference<T> target = new AtomicReference<T>();

    public T get() {
        return target.get();
    }

    public void set(T target) {
        checkNotNull(target, "Holded value must be non null");
        this.target.compareAndSet(null, target);
    }
}
