package ru.concerteza.util.tasks.impl;

/**
 * User: alexey
 * Date: 5/23/12
 */

import org.springframework.stereotype.Service;
import ru.concerteza.util.tasks.TaskEngine;

import javax.inject.Inject;

// engine sentinel
@Service
public class SuspensionChecker {
    @Inject
    private TaskEngine engine;

    public boolean isSuspended(long taskId) {
        return engine.isSuspended(taskId);
    }
}
