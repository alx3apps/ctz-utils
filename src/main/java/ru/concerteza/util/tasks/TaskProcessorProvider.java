package ru.concerteza.util.tasks;

/**
 * Interface is used by engine to get processor for given stage. String ID was chosen for spring-friendly API
 * For implementation example see {@link ru.concerteza.util.tasks.impl.TaskProcessorProviderImpl}
 *
 * @author alexey
 * Date: 5/17/12
 * @see TaskStageProcessor
 * @see TaskEngine
 */
@Deprecated // use com.alexkasko.tasks:task-engine
public interface TaskProcessorProvider {
    /**
     * Must provide {@link TaskStageProcessor} for given string id
     *
     * @param id processor id
     * @return stage processor
     */
    TaskStageProcessor provide(String id);
}
