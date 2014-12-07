package ru.concerteza.util.scheduler;

import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * User: alexkasko
 * Date: 12/7/14
 */
public class SchedulerCreatorTest {

    @Test
    public void test() throws InterruptedException {
        TestRunner tc = new TestRunner();
        ScheduledTaskRegistrar str = new SchedulerCreator(tc).createScheduler(ImmutableList.of(new TestTask(42, "* * * * * *")));
        // do actual scheduling, will be called by spring if bean registered
        str.afterPropertiesSet();
        Thread.sleep(2000);
        // will be called by spring if bean registered
        str.destroy();
        assertEquals(2, tc.created.size());
        assertEquals(42, tc.created.get(0).intValue());
        assertEquals(42, tc.created.get(1).intValue());
    }

    private static class TestTask implements ScheduledJob {
        final int id;
        final String cronExpr;

        TestTask(int id, String cronExpr) {
            this.id = id;
            this.cronExpr = cronExpr;
        }

        @Override
        public String getCronExpr() {
            return cronExpr;
        }
    }

    private static class TestRunner implements ScheduledJobRunner<TestTask> {

        final ArrayList<Integer> created = new ArrayList<Integer>();

        @Override
        public void runJob(TestTask st) {
            created.add(st.id);
        }
    }
}
