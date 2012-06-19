package ru.concerteza.util.concurrency;

import java.util.concurrent.*;

/**
 * By default {@link ThreadPoolExecutor} with unbounded queue will use threads only up to corePoolSize.
 * Combined with {@link SynchronousQueue} this handler forces thread pool to use threads up to <code>maximumPoolSize</code>
 * keeping input queue unbounded.
 *
 *
 * @author alexey
 * Date: 6/16/12
 * @see ThreadPoolExecutor
 */

public class UnboundedQueueRejectionHandler implements RejectedExecutionHandler {
    private final Executor queueExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new ExecutorThreadFactory("pool-rh"));

    /**
     * Puts rejected runnables into unlimited queue to allow growth of main executor
     *
     * @param r runnable rejected by main pool
     * @param executor main pool
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        queueExecutor.execute(new QueueRunnable(r, executor));
    }

    private class QueueRunnable implements Runnable {
        private final Runnable mainRunnable;
        private final Executor mainExecutor;

        private QueueRunnable(Runnable mainRunnable, Executor mainExecutor) {
            this.mainRunnable = mainRunnable;
            this.mainExecutor = mainExecutor;
        }

        @Override
        public void run() {
            mainExecutor.execute(mainRunnable);
        }
    }
}