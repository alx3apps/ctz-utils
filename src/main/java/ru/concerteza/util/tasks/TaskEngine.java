package ru.concerteza.util.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.newSetFromMap;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * Engine for asynchronous multistage suspendable tasks.
 * Processes task stages one by one using provided {@link java.util.concurrent.Executor},
 * tasks stage will be updated between stages processing. Task status will be updated on error,
 * suspend or after successful processing of last stage.
 * Processors must use {@link TaskEngine#isSuspended(long)} method periodically
 * and throw {@link TaskSuspendedException} on successful suspension check.
 *
 * @author alexey
 * Date: 5/17/12
 * @see Task
 * @see TaskManager
 * @see TaskProcessorProvider
 * @see TaskStageChain
 * @see TaskStageProcessor
 * @see TaskSuspendedException
 */
public class TaskEngine implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Executor executor;
    private TaskManager<? extends Task> manager;
    private TaskProcessorProvider provider;

    private final Set<Long> suspended = newSetFromMap(new ConcurrentHashMap<Long, Boolean>());
    private final Object fireLock = new Object();
    private final Object suspensionLock = new Object();

    /**
     * Spring friendly constructor
     */
    public TaskEngine() {
    }

    /**
     * @param executor executor will be used to process separate stages
     * @param manager tasks DAO for all task state operations
     * @param provider stage processors provider
     */
    public TaskEngine(Executor executor, TaskManager<? extends Task> manager, TaskProcessorProvider provider) {
        this.executor = executor;
        this.manager = manager;
        this.provider = provider;
    }

    /**
     * Init method, must be called after task manager init
     */
    public void postConstruct() {
        checkNotNull(executor, "'executor' must be non-null");
        checkNotNull(executor, "'dao must' be non-null");
        checkNotNull(executor, "'provider' must be non-null");
        synchronized (suspensionLock) {
            Collection<Long> tasks = manager.loadSuspendedIds();
            if(tasks.size() > 0) {
                logger.info("Suspended tasks cached: {}", tasks);
                suspended.addAll(tasks);
            }
        }
    }

    /**
     * Sends tasks provided by {@link ru.concerteza.util.tasks.TaskManager#markProcessingAndLoad()}
     * to execution
     *
     * @return count of tasks sent for processing
     */
    public int fire() {
        synchronized (fireLock) {
            Collection<? extends Task> tasksToFire = manager.markProcessingAndLoad();
            if(0 == tasksToFire.size()) {
                logger.debug("No tasks to fire, returning to sleep");
                return 0;
            }
            // fire tasks
            int counter = 0;
            for(Task task : tasksToFire) {
                checkNotNull(task, "'task' must be non-null");
                logger.debug("Firing task: '{}'", task);
                Runnable runnable = new StageRunnable(provider, manager, task);
                executor.execute(runnable);
                counter += 1;
            }
            if(counter > 0 ) logger.info("{} tasks fired", counter);
            return counter;
        }
    }

    /**
     * Spring scheduler friendly fire wrapper
     */
    @Override
    public void run() {
        fire();
    }

    /**
     * Mark task as suspended
     *
     * @param taskId task id
     * @return <code>true</code> if task wasn't already suspended
     */
    public boolean suspend(long taskId) {
        synchronized (suspensionLock) {
            boolean res = suspended.add(taskId);
            if(res) manager.updateStatusSuspended(taskId);
            return res;
        }
    }

    /**
     * Processors must use this method periodically and throw {@link TaskSuspendedException}
     * on successful suspension check. Will return <code>true</code> only once for given suspended taskId
     *
     * @param taskId task id
     * @return <code>true</code> if task was suspended and not successful check happened since that time
     */
    public boolean isSuspended(long taskId) {
        return suspended.remove(taskId);
    }

    /**
     * Alternative method to {@link #isSuspended(long)},
     * throws {@link TaskSuspendedException} on successful suspension check
     *
     * @param taskId task id
     * @throws TaskSuspendedException if task was already suspended
     */
    public void checkSuspended(long taskId) {
        if(suspended.remove(taskId)) throw new TaskSuspendedException(taskId);
    }

    /**
     * Spring3.1-friendly fluent setter
     *
     * @param executor executor will be used to process separate stages
     * @return engine itself for chained init
     */
    public TaskEngine setExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    /**
     * Spring3.1-friendly fluent setter
     *
     * @param manager tasks DAO for all task state operations
     * @return engine itself for chained init
     */
    public TaskEngine setTaskManager(TaskManager<? extends Task> manager) {
        this.manager = manager;
        return this;
    }

    /**
     * Spring3.1-friendly fluent setter
     *
     * @param provider stage processors provider
     * @return engine itself for chained init
     */
    public TaskEngine setTaskProcessorProvider(TaskProcessorProvider provider) {
        this.provider = provider;
        return this;
    }

    // Runnable instead of Callable is deliberate
    private class StageRunnable implements Runnable {
        private final TaskProcessorProvider provider;
        private final TaskManager dao;
        private final Task task;

        StageRunnable(TaskProcessorProvider provider, TaskManager dao, Task task) {
            checkNotNull(task.stageChain(), "Task must return non-null stageChain");
            this.provider = provider;
            this.dao = dao;
            this.task = task;
        }

        @Override
        public void run() {
            try {
                runStages();
            } catch (Exception e) {
                logger.error(format("System error running task: '{}'", task), e);
            }
        }

        @SuppressWarnings("unchecked")
        private void runStages() {
            final TaskStageChain chain = task.stageChain();
            TaskStageChain.Stage stage = chain.forName(task.getStageName());
            boolean markDefaultOnExit = true;
            while (chain.hasNext(stage)) {
                if (isSuspended(task.getId())) {
                    logger.info("Task, id: '{}' was suspended, terminating execution", task.getId());
                    markDefaultOnExit = false;
                    break;
                }
                stage = chain.next(stage);
                logger.debug("Starting stage: '{}' for task, id: '{}'", stage.getIntermediate(), task.getId());
                TaskStageProcessor processor = provider.provide(stage.getProcessorId());
                checkNotNull(provider, "Null processor returned for id: '%s'", stage.getProcessorId());
                dao.updateStage(task.getId(), stage.getIntermediate());
                try {
                    for(TaskStageListener li : processor.beforeListeners()) li.fire(task.getId());
                    processor.process(task.getId());
                    for(TaskStageListener li : processor.afterListeners()) li.fire(task.getId());
                    logger.debug("Stage: '{}' completed for task, id: '{}'", stage.getCompleted(), task.getId());
                    dao.updateStage(task.getId(), stage.getCompleted());
                } catch (TaskSuspendedException e) {
                    logger.info("Task, is: {} was suspended on stage: '{}'", task.getId(), stage.getIntermediate());
                    dao.updateStage(task.getId(), chain.previous(stage).getCompleted());
                    markDefaultOnExit = false;
                    break;
                } catch (Exception e) {
                    dao.updateStatusError(task.getId(), e, chain.previous(stage).getCompleted());
                    markDefaultOnExit = false;
                    break;
                }
            }
            if(markDefaultOnExit) dao.updateStatusDefault(task.getId());
        }
    }
}