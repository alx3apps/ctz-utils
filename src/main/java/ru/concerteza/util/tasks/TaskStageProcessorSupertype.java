package ru.concerteza.util.tasks;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Abstract processor supertype, added to make listeners methods implementation optional
 * for stage processors. Returns empty listeners lists.
 *
 * @author alexey
 * Date: 6/30/12
 */
public abstract class TaskStageProcessorSupertype implements TaskStageProcessor {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskStageListener> beforeListeners() {
        return ImmutableList.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskStageListener> afterListeners() {
        return ImmutableList.of();
    }
}
