package ru.concerteza.util.tasks;

/**
 * Interface for asynchronous multistage tasks.
 * It's implied, that task has two different fields to represent current state: stage and status.
 * Stage (e.g. 'loading_data', 'finished') is and task processing state,
 * status (e.g. 'error', 'processing') is a task application status
 * For implementation example see {@link ru.concerteza.util.tasks.impl.TaskImpl}
 *
 * @author alexey
 * Date: 5/17/12
 * @see TaskEngine
 * @see TaskStageChain
 * @see TaskManager
 */
@Deprecated // use com.alexkasko.tasks:task-engine
public interface Task {
    /**
     * Should be implemented as static method on upper level of hierarchy
     *
     * @return all stages this task may be into during processing
     */
    TaskStageChain stageChain();

    /**
     * @return  task instance unique id
     */
    long getId();

    /**
     * @return current stage for this task
     */
    String getStageName();
}
