package ru.concerteza.util.tasks;

/**
 * User: alexey
 * Date: 6/30/12
 */
public interface TaskStageListener {
    void fire(long taskId);
}
