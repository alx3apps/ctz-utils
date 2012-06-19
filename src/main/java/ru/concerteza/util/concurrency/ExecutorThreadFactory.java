package ru.concerteza.util.concurrency;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * Daemon thread factory, uses provided prefix for threads names, and keeps thread counter.
 * Thread-safe.
 *
 * @author alexey
 * Date: 10/11/11
 */
public class ExecutorThreadFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final String prefix;

    /**
     * Main constructor
     *
     * @param prefix thread name prefix
     */
    public ExecutorThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @param runnable thread runnable
     * @return daemon thread with <code>prefix-counter</code> name
     */
    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName(format("{}-{}", prefix, counter.incrementAndGet()));
        return thread;
    }
}
