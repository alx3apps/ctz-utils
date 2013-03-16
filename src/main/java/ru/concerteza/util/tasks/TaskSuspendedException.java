package ru.concerteza.util.tasks;

/**
 * This exception should be thrown by client's processor code on successful suspension check
 *
 * @author alexey
 * Date: 5/20/12
 * @see TaskEngine
 * @see Task
 */
@Deprecated // use com.alexkasko.tasks:task-engine
public class TaskSuspendedException extends RuntimeException {
    private static final long serialVersionUID = 3388511296426912332L;

    public TaskSuspendedException(long taskId) {
        super(Long.toString(taskId));
    }
}
