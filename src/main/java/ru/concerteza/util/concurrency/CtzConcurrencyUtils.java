package ru.concerteza.util.concurrency;

import org.apache.commons.lang.UnhandledException;

import java.util.concurrent.Callable;

/**
 * User: alexey
 * Date: 5/5/12
 */
public class CtzConcurrencyUtils {
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
                throw new UnhandledException(e);
            }
        }
    }
}
