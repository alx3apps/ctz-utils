package ru.concerteza.util.concurrency;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * User: alexey
 * Date: 5/20/12
 */
public class SameThreadExecutor implements ExecutorService {
    private boolean shuttedDown = false;

    @Override
    public void shutdown() {
        shuttedDown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        shuttedDown = true;
        Thread.currentThread().interrupt();
        return ImmutableList.of();
    }

    @Override
    public boolean isShutdown() {
        return shuttedDown;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        try {
            T res = task.call();
            return new SameThreadFuture<T>(res);
        } catch (Exception e) {
            return new SameThreadFuture<T>(e);
        }
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        try {
            task.run();
            return new SameThreadFuture<T>(result);
        } catch (Exception e) {
            return new SameThreadFuture<T>(e);
        }
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submit(task, null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        ImmutableList.Builder<Future<T>> builder = ImmutableList.builder();
        for(Callable<T> ca : tasks) {
            Future<T> res = submit(ca);
            builder.add(res);
        }
        return builder.build();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        StringBuilder errors = new StringBuilder();
        for (Callable<T> ta : tasks) {
            try {
                return ta.call();
            } catch (Exception e) {
                errors.append(e.getMessage()).append("\n");
            }
        }
        throw new ExecutionException(errors.toString(), new RuntimeException());
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    private class SameThreadFuture<T> implements Future<T> {
        private final T value;
        private final Exception exception;
        private boolean cancelled;

        private SameThreadFuture(Exception exception) {
            this.value = null;
            this.exception = exception;
        }

        private SameThreadFuture(T value) {
            this.value = value;
            this.exception = null;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            return false;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            if(null != exception) throw new ExecutionException(exception);
            return value;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if(null != exception) throw new ExecutionException(exception);
            return value;
        }
    }
}
