package ru.concerteza.util.concurrency;

import java.util.concurrent.Callable;

/**
 * Concurrency utils
 *
 * @author alexey
 * Date: 5/5/12
 */
public class CtzConcurrencyUtils {
    /**
     * Wraps {@link Callable} into {@link Runnable}, call result will be ignored,
     * exception will be rethrows as runtime exception
     *
     * @param callable callable to wrap
     * @return runnable
     */
    public static Runnable runnable(Callable<?> callable) {
        return new CallableWrapper(callable);
    }

    private static class CallableWrapper implements Runnable {
        private final Callable<?> callable;

        private CallableWrapper(Callable<?> callable) {
            this.callable = callable;
        }

        @Override
        public void run() {
            try {
                callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
