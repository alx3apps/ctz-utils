package ru.concerteza.util.concurrency;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.UnhandledException;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 7/6/12
 */
public class LimitedExecutorServiceWrapper implements ExecutorService {
    private final ExecutorService target;
    private final Semaphore semaphore;

    public LimitedExecutorServiceWrapper(ExecutorService executor, int parallelLimit) {
        checkNotNull(executor, "Provided executor is null");
        checkArgument(parallelLimit > 0, "Limit mast be positive but was: '%s'", parallelLimit);
        this.target = executor;
        this.semaphore = new Semaphore(parallelLimit);
    }

    @Override
    public void shutdown() {
        target.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return target.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return target.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return target.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return target.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return target.submit(new SemaphoreCallable<T>(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return target.submit(new SemaphoreRunnable(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return target.submit(new SemaphoreRunnable(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return target.invokeAll(Collections2.transform(tasks, new SemaphoreCallableFun<T>()));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return target.invokeAll(Collections2.transform(tasks, new SemaphoreCallableFun<T>()), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return target.invokeAny(Collections2.transform(tasks, new SemaphoreCallableFun<T>()));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return target.invokeAny(Collections2.transform(tasks, new SemaphoreCallableFun<T>()), timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        target.execute(new SemaphoreRunnable(command));
    }

    private class SemaphoreCallable<T> implements Callable<T> {
        private final Callable<T> target;

        private SemaphoreCallable(Callable<T> target) {
            checkNotNull(target, "Provided callable is null");
            this.target = target;
        }

        @Override
        public T call() throws Exception {
            try {
                semaphore.acquire();
                return target.call();
            } finally {
                semaphore.release();
            }
        }
    }

    private class SemaphoreRunnable implements Runnable {
        private final Runnable target;

        private SemaphoreRunnable(Runnable target) {
            this.target = target;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                target.run();
            } catch(InterruptedException e) {
                throw new UnhandledException(e);
            } finally {
                semaphore.release();
            }
        }
    }

    private class SemaphoreCallableFun<T> implements Function<Callable<T>, SemaphoreCallable<T>> {
        @SuppressWarnings("unchecked")
        @Override
        public SemaphoreCallable<T> apply(@Nullable Callable<T> input) {
            return new SemaphoreCallable<T>(input);
        }
    }
}