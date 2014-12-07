package ru.concerteza.util.scheduler;

/**
 * Interface for tasks templates
 *
 * User: alexkasko
 * Date: 12/7/14
 */
public interface ScheduledJob {

    /**
     * Must return cron expression to use for this task
     *
     * @return cron expression
     */
    String getCronExpr();
}
