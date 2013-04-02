package ru.concerteza.util.io.finishable;

import com.google.common.base.Function;

/**
 * Interface for IO operations that should be explicitly closed by the caller
 * differently depending of app state
 *
 * @author alexkasko
 * Date: 4/2/13
 */
public interface Finishable<F, T> {
    /**
     * Should be called instead of {@code close()}
     *
     * @param fun any operations or data
     */
    void finish(Function<F, T> fun);
}
