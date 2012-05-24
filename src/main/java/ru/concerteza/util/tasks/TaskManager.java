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
public interface TaskManager<T extends Task> {
    /**
     * This method is used to load new and resumed tasks to run.
     * All returning tasks should be switched into 'processing' status in DB.
     * It's implementation responsibility to not return tasks that may be running in that moment.
     *
     * @return collection of tasks to run
     */
    Collection<T> markProcessingAndLoad();

    /**
     * This method will be used to cache all suspended task ids on engine startup.
     *
     * @return collection of suspended tasks ids
     */
    Collection<Long> loadSuspendedIds();

    /**
     * Changes task stage
     *
     * @param taskId task id
     * @param stage new stage
     */
    void updateStage(long taskId, String stage);

    /**
     *
     *
     * @param taskId
     */
    void updateStatusDefault(long taskId);

    void updateStatusSuspended(long taskId);

    void updateStatusError(long taskId, Exception e, String lastCompletedStage);
}
