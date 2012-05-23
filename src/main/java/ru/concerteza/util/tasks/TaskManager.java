package ru.concerteza.util.tasks;

import java.util.Collection;

/**
 * User: alexey
 * Date: 5/17/12
 */
public interface TaskManager<T extends Task> {
    Collection<T> markProcessingAndLoad();

    Collection<Long> loadSuspendedIds();

    void updateStage(long taskId, String stage);

    void updateStatusDefault(long taskId);

    void updateStatusSuspended(long taskId);

    void updateStatusError(long taskId, Exception e, String lastCompletedStage);
}
