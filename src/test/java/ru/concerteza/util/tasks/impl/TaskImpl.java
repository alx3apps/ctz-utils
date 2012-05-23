package ru.concerteza.util.tasks.impl;

import com.google.common.base.Function;
import ru.concerteza.util.tasks.Task;
import ru.concerteza.util.tasks.TaskStageChain;

import javax.persistence.*;

import static ru.concerteza.util.tasks.impl.Stage.*;
import static ru.concerteza.util.tasks.impl.Status.NORMAL;

/**
* User: alexey
* Date: 5/22/12
*/

@Entity
@Table(name = "tasks")
@SequenceGenerator(name = "tasks_id_seq", sequenceName = "tasks_id_seq")
public class TaskImpl implements Task {
    public static final Function<TaskImpl, Long> ID_FUNCTION = new IdFunction();

    @Id
    @GeneratedValue(generator = "tasks_id_seq", strategy = GenerationType.SEQUENCE)
    @Column
    private long id;
    @Column
    @Enumerated(EnumType.STRING)
    private Stage stage;
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column
    private long payload;

    public TaskImpl() {
    }

    public TaskImpl(long payload) {
        this.stage = CREATED;
        this.status = NORMAL;
        this.payload = payload;
    }

    public static TaskStageChain chain() {
        return TaskStageChain.builder(CREATED.name())
                .add(RUNNING.name(), DATA_LOADED.name(), DataStageProcessor.class.getSimpleName())
                .add(REPORTS.name(), FINISHED.name(), ReportStageProcessor.class.getSimpleName())
                .build();
    }

    @Override
    public TaskStageChain stageChain() {
        return chain();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getStage() {
        return stage.name();
    }

    public Status getStatus() {
        return status;
    }

    public long getPayload() {
        return payload;
    }

    public void changeStage(Stage stage) {
        this.stage = stage;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

    private static class IdFunction implements Function<TaskImpl, Long> {
        @Override
        public Long apply(TaskImpl input) {
            return input.getId();
        }
    }
}