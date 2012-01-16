package ru.concerteza.util.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 10/11/11
 */
public class ExecutorThreadFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName(format("worker-{}", counter.incrementAndGet()));
        return thread;
    }
}
