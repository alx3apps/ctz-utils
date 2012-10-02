package ru.concerteza.util.collection.finishable;

import com.google.common.base.Function;

import java.io.Closeable;

/**
 * Guava Function extension that will be called on iterator exhaustion
 *
 * @author alexey
 * Date: 10/2/12
 * @see FinishableIterator
 */
public interface FinishableFunction<F, T> extends Function<F, T> {
    /**
     * Will be called no more then once on iterator exhaustion
     */
    void finish();
}
