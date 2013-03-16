package ru.concerteza.util.tasks;

/**
 * Additional operations that will be executed before/after task stage processing.
 * Listeners are separated from task processors to (among others)
 * simplify their declarative transaction management.
 *
 * @author alexey
 * Date: 6/30/12
 * @see TaskStageProcessor
 * @see TaskStageProcessorSupertype
 * @see TaskEngine
 */
@Deprecated // use com.alexkasko.tasks:task-engine
public interface TaskStageListener {
    void fire(long taskId);
}
