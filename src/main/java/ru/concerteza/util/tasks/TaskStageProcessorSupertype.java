package ru.concerteza.util.tasks;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * User: alexey
 * Date: 6/30/12
 */
public abstract class TaskStageProcessorSupertype implements TaskStageProcessor {
    @Override
    public List<TaskStageListener> beforeListeners() {
        return ImmutableList.of();
    }

    @Override
    public List<TaskStageListener> afterListeners() {
        return ImmutableList.of();
    }
}
