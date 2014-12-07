package ru.concerteza.util.scheduler;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import ru.concerteza.util.concurrency.ExecutorThreadFactory;

import java.util.Collection;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * Utility class to create Spring's scheduler
 *
 * User: alexkasko
 * Date: 12/7/14
 */
public class SchedulerCreator {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerCreator.class);

    private final ScheduledJobRunner jobRunner;
    private final int schedulerPoolSize;
    private final ThreadFactory threadFactory;

    /**
     * Shortcut constructor
     *
     * @param jobRunner task creator implementation
     */
    public SchedulerCreator(ScheduledJobRunner jobRunner) {
        this(jobRunner, 1, new ExecutorThreadFactory("sch"));
    }

    /**
     * Constructor
     *
     * @param jobRunner task creator implementation
     * @param schedulerPoolSize pool size for schedulers private thread pool
     * @param threadFactory thread factory for schedulers pool
     */
    public SchedulerCreator(ScheduledJobRunner jobRunner, int schedulerPoolSize, ThreadFactory threadFactory) {
        this.jobRunner = jobRunner;
        this.schedulerPoolSize = schedulerPoolSize;
        this.threadFactory = threadFactory;
    }

    /**
     * Creates spring scheduler from the task templates list,
     * created scheduler must be registered in context as {@code @Bean}
     *
     * @param scheduledTasks list of tasks templates
     * @param <T> tasks type
     * @return scheduler instance
     */
    public <T extends ScheduledJob> ScheduledTaskRegistrar createScheduler(Collection<T> scheduledTasks) {
        logger.info("Scheduling registered tasks: [{}]",scheduledTasks);
        ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(schedulerPoolSize, threadFactory);
        ConcurrentTaskScheduler cts = new ConcurrentTaskScheduler(stpe);
        ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
        registrar.setTaskScheduler(cts);
        ImmutableMap.Builder<Runnable,String> builder = ImmutableMap.builder();
        for(ScheduledJob st : scheduledTasks) {
            builder.put(new TaskCreatorRunnable(jobRunner, st), st.getCronExpr());
        }
        registrar.setCronTasks(builder.build());
        return registrar;
    }

    private static class TaskCreatorRunnable implements Runnable {

        private final ScheduledJobRunner taskCreator;
        private final ScheduledJob st;

        private TaskCreatorRunnable(ScheduledJobRunner taskCreator, ScheduledJob st) {
            this.taskCreator = taskCreator;
            this.st = st;
        }

        @Override
        public void run() {
            taskCreator.runJob(st);
        }
    }
}
