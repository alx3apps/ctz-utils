package ru.concerteza.util.tasks;

/**
 * User: alexey
 * Date: 5/17/12
 */
public interface TaskStageProcessor {
    void process(long taskId) throws Exception;
}
