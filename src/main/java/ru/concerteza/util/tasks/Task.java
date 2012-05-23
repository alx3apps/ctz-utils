package ru.concerteza.util.tasks;

/**
 * User: alexey
 * Date: 5/17/12
 */
public interface Task {
    // should be implemented as static method on upper level of hierarchy
    TaskStageChain stageChain();

    long getId();

    String getStage();
}
