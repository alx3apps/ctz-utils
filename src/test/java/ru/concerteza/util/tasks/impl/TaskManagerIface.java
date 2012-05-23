package ru.concerteza.util.tasks.impl;

import ru.concerteza.util.tasks.TaskManager;

/**
 * User: alexey
 * Date: 5/23/12
 */
public interface TaskManagerIface extends TaskManager<TaskImpl> {
    public long add(TaskImpl task);

    public TaskImpl load(long taskId);

    public void resume(long taskId);
}
