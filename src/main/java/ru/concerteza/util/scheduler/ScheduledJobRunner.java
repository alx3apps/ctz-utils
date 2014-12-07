package ru.concerteza.util.scheduler;

/**
 * Provided interface to create business tasks from scheduled task's descriptions
 *
 * User: alexkasko
 * Date: 12/7/14
 */
public interface ScheduledJobRunner<T extends ScheduledJob> {

    /**
     * Should create a business task from scheduled task's descriptions
     *
     * @param st scheduled task's descriptions
     */
    void runJob(T st);
}
