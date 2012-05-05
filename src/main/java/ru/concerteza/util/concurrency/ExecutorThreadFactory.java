package ru.concerteza.util.concurrency;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 10/11/11
 */
public class ExecutorThreadFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final String prefix;

    public ExecutorThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName(format("{}-{}", prefix, counter.incrementAndGet()));
        return thread;
    }
}
