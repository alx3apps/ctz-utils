package ru.concerteza.util.tasks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import ru.concerteza.util.concurrency.SameThreadExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.string.CtzFormatUtils.format;


/**
 * User: alexey
 * Date: 5/19/12
 */

public class TaskEngineTest {
    private final List<String> events = newArrayList();
    private final TaskDAOImpl dao = new TaskDAOImpl();
    private final TaskEngine taskService = new TaskEngine(new SameThreadExecutor(), dao, new ProcessorProvider());
    private final Map<Long, TestTask> tasks = ImmutableMap.of(42L, new TestTask(42), 43L, new TestTask(43), 44L, new TestTask(44));
    private boolean task43wasSuspendedOnce = false;

    @Test
    public void test() throws InterruptedException {
        dao.nextTaskId = 42;
        taskService.fire();
        assertEquals("Finish fail", "finished", tasks.get(42L).stage);
        dao.nextTaskId = 43;
        taskService.fire();
        assertEquals("Suspend fail", "data_loaded", tasks.get(43L).stage);
//        taskService.resume(43); business logic resume is implied here
        taskService.fire();
        assertEquals("Resume fail", "finished", tasks.get(43L).stage);
        dao.nextTaskId = 44;
        taskService.fire();
        assertEquals("Exception fail", "data_loaded", tasks.get(44L).stage);

//        assertEquals("Events size fail", 25, events.size());
        // 42
        assertEquals("Event fail", "TaskDAO.task.42.status.processing", events.get(0));
        assertEquals("Event fail", "TaskDAO.task.42.stage.running_data", events.get(1));
        assertEquals("Event fail", "DataProcessor.task.42", events.get(2));
        assertEquals("Event fail", "TaskDAO.task.42.stage.data_loaded", events.get(3));
        assertEquals("Event fail", "TaskDAO.task.42.stage.running_reports", events.get(4));
        assertEquals("Event fail", "ReportsProcessor.task.42", events.get(5));
        assertEquals("Event fail", "TaskDAO.task.42.stage.finished", events.get(6));
        assertEquals("Event fail", "TaskDAO.task.42.status.default", events.get(7));
        // 43
        assertEquals("Event fail", "TaskDAO.task.43.status.processing", events.get(8));
        assertEquals("Event fail", "TaskDAO.task.43.stage.running_data", events.get(9));
        assertEquals("Event fail", "DataProcessor.task.43", events.get(10));
        assertEquals("Event fail", "TaskDAO.task.43.stage.data_loaded", events.get(11));
        assertEquals("Event fail", "TaskDAO.task.43.stage.running_reports", events.get(12));
        assertEquals("Event fail", "TaskDAO.task.43.status.suspended", events.get(13));
        assertEquals("Event fail", "TaskDAO.task.43.stage.data_loaded", events.get(14));
        assertEquals("Event fail", "TaskDAO.task.43.status.processing", events.get(15));
        assertEquals("Event fail", "TaskDAO.task.43.stage.running_reports", events.get(16));
        assertEquals("Event fail", "ReportsProcessor.task.43", events.get(17));
        assertEquals("Event fail", "TaskDAO.task.43.stage.finished", events.get(18));
        assertEquals("Event fail", "TaskDAO.task.43.status.default", events.get(19));
        // 44
        assertEquals("Event fail", "TaskDAO.task.44.status.processing", events.get(20));
        assertEquals("Event fail", "TaskDAO.task.44.stage.running_data", events.get(21));
        assertEquals("Event fail", "DataProcessor.task.44", events.get(22));
        assertEquals("Event fail", "TaskDAO.task.44.stage.data_loaded", events.get(23));
        assertEquals("Event fail", "TaskDAO.task.44.stage.running_reports", events.get(24));
        assertEquals("Event fail", "TaskDAO.task.44.status.error", events.get(25));
        assertEquals("Event fail", "TaskDAO.task.44.stage.data_loaded", events.get(26));
    }

    private class TestTask implements Task {
        private final long id;
        private String stage;
        private String status;

        private TestTask(long id) {
            this.id = id;
            this.stage = "created";
            this.status = "default";
        }

        @Override
        public TaskStageChain stageChain() {
            return TaskStageChain.builder("created")
                    .add("running_data", "data_loaded", DataProcessor.class.getName())
                    .add("running_reports", "finished", ReportsProcessor.class.getName())
                    .build();
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public String getStageName() {
            return stage;
        }

        public void changeStage(String stage) {
            this.stage = stage;
        }

        public String getStatus() {
            return status;
        }

        public void changeStatus(String status) {
            this.status = status;
        }
    }

    private class DataProcessor extends TaskStageProcessorSupertype {
        @Override
        public void process(long taskId) throws Exception {
            if(taskService.isSuspended(taskId)) throw new TaskSuspendedException(taskId);
            events.add(format("DataProcessor.task.{}", taskId));
        }
    }

    private class ReportsProcessor extends TaskStageProcessorSupertype {
        @Override
        public void process(long taskId) throws Exception {
            if(43 == taskId && !task43wasSuspendedOnce) {
                taskService.suspend(43);
                task43wasSuspendedOnce = true;
            }
            if(taskService.isSuspended(taskId)) throw new TaskSuspendedException(taskId);
            if(44 == taskId) throw new RuntimeException("44 is a fail number");
            events.add(format("ReportsProcessor.task.{}", taskId));
        }
    }

    private class TaskDAOImpl implements TaskManager<TestTask> {
        private long nextTaskId;

        @Override
        public Collection<TestTask> markProcessingAndLoad() {
            TestTask task = tasks.get(nextTaskId);
            task.changeStatus("processing");
            events.add(format("TaskDAO.task.{}.status.processing", task.getId()));
            return ImmutableList.of(task);
        }

        @Override
        public Collection<Long> loadSuspendedIds() {
            throw new NotImplementedException();
        }

        @Override
        public void updateStage(long taskId, String stage) {
            tasks.get(taskId).changeStage(stage);
            events.add(format("TaskDAO.task.{}.stage.{}", taskId, stage));
        }

        @Override
        public void updateStatusDefault(long taskId) {
            tasks.get(taskId).changeStatus("default");
            events.add(format("TaskDAO.task.{}.status.default", taskId));
        }

        @Override
        public void updateStatusSuspended(long taskId) {
            tasks.get(taskId).changeStatus("suspended");
            events.add(format("TaskDAO.task.{}.status.suspended", taskId));
        }

        @Override
        public void updateStatusError(long taskId, Exception e, String lastFinishedStage) {
            tasks.get(taskId).changeStatus("error");
            events.add(format("TaskDAO.task.{}.status.error", taskId, e.getMessage()));
            tasks.get(taskId).changeStage(lastFinishedStage);
            events.add(format("TaskDAO.task.{}.stage.{}", taskId, lastFinishedStage));
        }
    }

    private class ProcessorProvider implements TaskProcessorProvider {
        @Override
        public TaskStageProcessor provide(String id) {
            if(DataProcessor.class.getName().equals(id)) return new DataProcessor();
            if(ReportsProcessor.class.getName().equals(id)) return new ReportsProcessor();
            throw new IllegalArgumentException("Cannot happen");
        }
    }
}
