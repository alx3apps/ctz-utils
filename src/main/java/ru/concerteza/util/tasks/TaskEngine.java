package ru.concerteza.util.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.newSetFromMap;
import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 5/17/12
 */
public class TaskEngine {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Executor executor;
    private TaskManager<? extends Task> manager;
    private TaskProcessorProvider provider;

    private final Set<Long> suspended = newSetFromMap(new ConcurrentHashMap<Long, Boolean>());
    private final Object fireLock = new Object();
    private final Object suspensionLock = new Object();

    public TaskEngine() {
    }

    public TaskEngine(Executor executor, TaskManager<? extends Task> manager, TaskProcessorProvider provider) {
        this.executor = executor;
        this.manager = manager;
        this.provider = provider;
    }

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

    public boolean suspend(long id) {
        synchronized (suspensionLock) {
            boolean res = suspended.add(id);
            if(res) manager.updateStatusSuspended(id);
            return res;
        }
    }

    public boolean isSuspended(long id) {
        return suspended.remove(id);
    }

    // spring friendly setters

    public TaskEngine setExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    public TaskEngine setTaskManager(TaskManager<? extends Task> manager) {
        this.manager = manager;
        return this;
    }

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
            TaskStageChain.Stage stage = chain.forName(task.getStage());
            boolean markDefaultOnExit = true;
            while (chain.hasNext(stage)) {
                if (isSuspended(task.getId())) {
                    logger.info("Task, id: '{}' was suspended, terminating execution", task.getId());
                    break;
                }
                stage = chain.next(stage);
                logger.debug("Starting stage: '{}' for task, id: '{}'", stage.getIntermediate(), task.getId());
                TaskStageProcessor processor = provider.provide(stage.getProcessorId());
                checkNotNull(provider, "Null processor returned for id: '%s'", stage.getProcessorId());
                dao.updateStage(task.getId(), stage.getIntermediate());
                try {
                    processor.process(task.getId());
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