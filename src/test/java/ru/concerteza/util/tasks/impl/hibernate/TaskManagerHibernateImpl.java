package ru.concerteza.util.tasks.impl.hibernate;

import com.google.common.collect.*;
import java.util.*;
import javax.inject.Inject;
import org.hibernate.*;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.concerteza.util.tasks.impl.*;

/**
 * User: alexey
 * Date: 5/23/12
 */

// query literals are deliberate
@Service
public class TaskManagerHibernateImpl implements TaskManagerIface {
    @Inject
    private SessionFactory sf;
    private Session cs() {return sf.getCurrentSession();}

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public Collection<TaskImpl> markProcessingAndLoad() {
        // lock selected
        int updated = cs().createSQLQuery("update tasks set status='SELECTED' where (status='NORMAL' and stage='CREATED') or status='RESUMED'")
                .executeUpdate();
        if(0 == updated) return ImmutableList.of();
        // load selected
        List<TaskImpl> taskList = cs().createQuery("from TaskImpl where status='SELECTED'").list();
        List<Long> taskIds = Lists.transform(taskList, TaskImpl.ID_FUNCTION);
        // mark
        cs().createSQLQuery("update tasks set status='PROCESSING' where id in (:taskIds)")
                .setParameterList("taskIds", taskIds)
                .executeUpdate();
        // returned status is not synchronized with db, but that doesn't matter
        return taskList;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Collection<Long> loadSuspendedIds() {
        return cs().createSQLQuery("select id from tasks where status='SUSPENDED'")
                .addScalar("id", new LongType())
                .list();
    }

    @Override
    @Transactional
    public void updateStage(long taskId, String stage) {
        Stage st = Stage.valueOf(stage);
        TaskImpl task = (TaskImpl) cs().get(TaskImpl.class, taskId);
        task.changeStage(st);
        cs().update(task);
    }

    @Override
    @Transactional
    public void updateStatusDefault(long taskId) {
        TaskImpl task = (TaskImpl) cs().get(TaskImpl.class, taskId);
        task.changeStatus(Status.NORMAL);
        cs().update(task);
    }

    @Override
    @Transactional
    public void updateStatusSuspended(long taskId) {
        TaskImpl task = (TaskImpl) cs().get(TaskImpl.class, taskId);
        task.changeStatus(Status.SUSPENDED);
        cs().update(task);
    }

    @Override
    @Transactional
    public void updateStatusError(long taskId, Exception e, String lastCompletedStage) {
        e.printStackTrace();
        Stage stage = Stage.valueOf(lastCompletedStage);
        TaskImpl task = (TaskImpl) cs().get(TaskImpl.class, taskId);
        task.changeStatus(Status.ERROR);
        task.changeStage(stage);
    }

    @Override
    @Transactional
    public long add(TaskImpl task) {
        return (Long) cs().save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskImpl load(long taskId) {
        return (TaskImpl) cs().get(TaskImpl.class, taskId);
    }

    @Override
    @Transactional
    public void resume(long taskId) {
        TaskImpl task = (TaskImpl) cs().get(TaskImpl.class, taskId);
        task.changeStatus(Status.RESUMED);
        cs().update(task);
    }
}
