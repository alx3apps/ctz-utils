package ru.concerteza.util.tasks;

/**
 * User: alexey
 * Date: 5/17/12
 */
public interface TaskProcessorProvider {
    TaskStageProcessor provide(String id);
}
