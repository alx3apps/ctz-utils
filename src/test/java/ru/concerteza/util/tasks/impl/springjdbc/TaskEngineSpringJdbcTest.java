package ru.concerteza.util.tasks.impl.springjdbc;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.concerteza.util.concurrency.SameThreadExecutor;
import ru.concerteza.util.tasks.TaskEngine;
import ru.concerteza.util.tasks.TaskProcessorProvider;
import ru.concerteza.util.tasks.impl.TaskImpl;
import ru.concerteza.util.tasks.impl.TaskManagerIface;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 5/23/12
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TaskEngineSpringJdbcTest.Config.class)
public class TaskEngineSpringJdbcTest {
    @Inject
    private TaskEngine taskEngine;
    @Inject
    private TaskManagerIface taskManager;
    @Inject
    private DataSource ds;

    @Test
    public void test() throws InterruptedException {
        JdbcTemplate jt = new JdbcTemplate(ds);
        // finish
        for(int i=0; i<42; i++) taskManager.add(new TaskImpl(0));
        taskEngine.fire();
        int finished = jt.queryForInt("select count(id) from tasks where stage='FINISHED'");
        assertEquals("Finish fail", 42, finished);
    }

    @Configuration
    @ComponentScan(basePackages = "ru.concerteza.util.tasks.impl",
            excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, value = NoHibernateImplFilter.class))
    @EnableTransactionManagement
    static class Config {
        @Inject private TaskManagerIface taskManager;
        @Inject private TaskProcessorProvider processorProvider;

        @Bean(initMethod = "postConstruct")
        public TaskEngine taskEngine() {
            return new TaskEngine(new SameThreadExecutor(), taskManager, processorProvider);
        }

        @Bean(destroyMethod = "close")
        public DataSource dataSource() {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem:foo");
            return ds;
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            DataSource ds = dataSource();
            JdbcTemplate jt = new JdbcTemplate(ds);
            jt.execute("create table tasks(id bigint primary key, stage varchar(255), status varchar(255), payload bigint)");
            jt.execute("create sequence tasks_id_seq");
            return new DataSourceTransactionManager(ds);
        }

    }

    private static class NoHibernateImplFilter extends RegexPatternTypeFilter {
        private NoHibernateImplFilter() {
            super(Pattern.compile("ru\\.concerteza\\.util\\.tasks\\.impl\\.hibernate\\..+"));
        }
    }
}
