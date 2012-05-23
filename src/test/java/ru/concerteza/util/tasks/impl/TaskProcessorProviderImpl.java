package ru.concerteza.util.tasks.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.concerteza.util.tasks.TaskProcessorProvider;
import ru.concerteza.util.tasks.TaskStageProcessor;

import javax.inject.Inject;

/**
 * User: alexey
 * Date: 5/23/12
 */

@Service
public class TaskProcessorProviderImpl implements TaskProcessorProvider {
    @Inject
    private ApplicationContext ctx;

    @Override
    public TaskStageProcessor provide(String id) {
        return ctx.getBean(id, TaskStageProcessor.class);
    }
}
