package ru.concerteza.util.tasks;

import java.util.Collection;

/**
 * DAO interface for tasks
 * For implementation examples see {@link ru.concerteza.util.tasks.impl.hibernate.TaskManagerHibernateImpl}
 * and {@link ru.concerteza.util.tasks.impl.springjdbc.TaskManagerSpringJdbcImpl}
 *
 * @author alexey
 * Date: 5/17/12
 * @see TaskEngine
 * @see Task
 */
@Deprecated // use com.alexkasko.tasks:task-engine
public interface TaskManager<T extends Task> {
    /**
     * This method is used to load new and resumed tasks to run.
     * All returning tasks should be switched into 'processing' status in DB.
     * It's implementation responsibility to not return tasks that may be running in that moment.
     *
     * @return collection of tasks to run
     */
    Collection<? extends T> markProcessingAndLoad();

    /**
     * This method will be used to cache all suspended task ids on engine startup.
     *
     * @return collection of suspended tasks ids
     */
    Collection<Long> loadSuspendedIds();

    /**
     * Changes task stage, will be called between stages processing
     *
     * @param taskId task id
     * @param stage new stage
     */
    void updateStage(long taskId, String stage);

    /**
     * Changes task status to default, will be called after successful processing of last stage
     *
     * @param taskId task id
     */
    void updateStatusDefault(long taskId);

    /**
     * Changes task status to default, will be called after on task suspend
     *
     * @param taskId task id
     */
    void updateStatusSuspended(long taskId);

    /**
     * Changes task status into "error" and task stage into last completed stage
     *
     * @param taskId task id
     * @param e exception
     * @param lastCompletedStage name of last completed stage
     */
    void updateStatusError(long taskId, Exception e, String lastCompletedStage);
}
