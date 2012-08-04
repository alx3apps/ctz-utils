package ru.concerteza.util.tasks;

import java.util.List;

/**
 * Interface for separate units of business oriented work on task
 *
 * @author alexey
 * Date: 5/17/12
 * @see TaskEngine
 * @see TaskEngine#isSuspended(long)
 * @see Task
 * @see TaskProcessorProvider
 */
public interface TaskStageProcessor {

    /**
     * Some business oriented work to do on task.
     * {@link TaskEngine#isSuspended(long)} method should be called periodically,
     * {@link TaskSuspendedException} must be thrown on successful suspension check
     *
     * @param taskId task id
     * @throws TaskSuspendedException task will rolled back to last completed stage and switched into 'suspended' status
     * @throws Exception task will rolled back to last completed stage and switched into 'error' status
     */
    void process(long taskId) throws Exception;

    /**
     * List of additional operations, that will be called by {@link TaskEngine} before stage processing
     *
     * @return before listeners
     */
    List<? extends TaskStageListener> beforeListeners();

    /**
     * List of additional operations, that will be called by {@link TaskEngine} after stage processing
     *
     * @return after listeners
     */
    List<? extends TaskStageListener> afterListeners();
}
