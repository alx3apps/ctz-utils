package ru.concerteza.util.tasks.impl.hibernate;

import com.google.common.collect.Lists;
import java.util.*;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.concerteza.util.tasks.*;
import ru.concerteza.util.tasks.impl.*;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.junit.Assert.*;

/**
 * User: alexey
 * Date: 5/23/12
 */

// long multithreaded test, may fail like 'Finish all fail expected:<45> but was:<42>'
// because of race conditions in test checks
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TaskEngineHibernateTest.Config.class)
public class TaskEngineHibernateTest {
    @Inject
    private TaskEngine taskEngine;
    @Inject
    private TaskManagerIface taskManager;
    @Inject
    private DataSource ds;

    @Test
    public void dummmy() {
//      I'm dummy
    }

//    @Test
    public void test() throws InterruptedException {
        JdbcTemplate jt = new JdbcTemplate(ds);
        // finish
        for (int i = 0; i < 42; i++) taskManager.add(new TaskImpl(0));
        taskEngine.fire();
        Thread.sleep(600);
        int finished = jt.queryForInt("select count(id) from tasks where stage='FINISHED'");
        assertEquals("Finish fail", 42, finished);
        // suspend
        List<Long> forSuspend = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            long id = taskManager.add(new TaskImpl(500));
            forSuspend.add(id);
        }
        taskEngine.fire();
        Thread.sleep(100); // pass engine suspension checker
        for (long id : forSuspend) {
            boolean res = taskEngine.suspend(id);
            assertTrue("Suspend fail", res);
        }
        Thread.sleep(100); // wait for tasks to marked suspended
        int suspended = jt.queryForInt("select count(id) from tasks where status='SUSPENDED'");
        assertEquals("Suspend fail", 3, suspended);
        // resume
        for (long id : forSuspend) taskManager.resume(id);
        taskEngine.fire();
        Thread.sleep(2000);
        int finishedAll = jt.queryForInt("select count(id) from tasks where stage='FINISHED'");
        assertEquals("Finish all fail", 45, finishedAll);
    }

    @Configuration
    @ComponentScan(basePackages = "ru.concerteza.util.tasks.impl",
            excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, value = NoSpringJdbcImplFilter.class))
    @EnableTransactionManagement
    static class Config {
        @Inject private TaskManagerIface taskManager;
        @Inject private TaskProcessorProvider processorProvider;

        @Bean(initMethod = "postConstruct")
        public TaskEngine taskEngine() {
            return new TaskEngine(newCachedThreadPool(), taskManager, processorProvider);
        }

        @Bean(destroyMethod = "close")
        public DataSource dataSource() {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem:bar");
            return ds;
        }

        @Bean
        public LocalSessionFactoryBean sessionFactory() {
            LocalSessionFactoryBean fac = new LocalSessionFactoryBean();
            fac.setDataSource(dataSource());
            fac.setAnnotatedClasses(TaskImpl.class);
            Properties hiber = new Properties();
            String dialect = "org.hibernate.dialect.H2Dialect";
            hiber.setProperty("hibernate.dialect", dialect);
            hiber.setProperty("hibernate.hbm2ddl.auto", "create");
            fac.setHibernateProperties(hiber);
            return fac;
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            return new HibernateTransactionManager(sessionFactory().getObject());
        }

    }

    private static class NoSpringJdbcImplFilter extends RegexPatternTypeFilter {
        private NoSpringJdbcImplFilter() {
            super(Pattern.compile("ru\\.concerteza\\.util\\.tasks\\.impl\\.springjdbc\\..+"));
        }
    }
}
