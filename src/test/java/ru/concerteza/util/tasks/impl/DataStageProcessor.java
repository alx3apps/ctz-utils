package ru.concerteza.util.tasks.impl;

import org.springframework.stereotype.Service;
import ru.concerteza.util.tasks.TaskStageProcessor;
import ru.concerteza.util.tasks.TaskSuspendedException;

import javax.inject.Inject;

import static java.lang.System.currentTimeMillis;

/**
 * User: alexey
 * Date: 5/23/12
 */
public interface DataStageProcessor extends TaskStageProcessor {
}

@Service("DataStageProcessor")
class DataStageProcessorImpl implements DataStageProcessor {
    @Inject
    private TaskManagerIface taskManager;
    @Inject
    private SuspensionChecker checker;

    @Override
    public void process(long taskId) throws Exception {
        long awaitTime = taskManager.load(taskId).getPayload();
        long start = currentTimeMillis();
        while (currentTimeMillis() - start < awaitTime) {
            if(checker.isSuspended(taskId)) throw new TaskSuspendedException(taskId);
            Thread.sleep(100);
        }
    }
}
