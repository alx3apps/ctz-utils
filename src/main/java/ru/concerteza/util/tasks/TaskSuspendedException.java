package ru.concerteza.util.tasks;

/**
 * User: alexey
 * Date: 5/20/12
 */
public class TaskSuspendedException extends RuntimeException {
    private static final long serialVersionUID = 3388511296426912332L;

    public TaskSuspendedException(long taskId) {
        super(Long.toString(taskId));
    }
}
