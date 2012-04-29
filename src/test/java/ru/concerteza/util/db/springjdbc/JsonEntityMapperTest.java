package ru.concerteza.util.db.springjdbc;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.concerteza.util.CtzReflectionUtils;
import ru.concerteza.util.db.springjdbc.filter.LocalDateTimeFilter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: alexey
 * Date: 4/29/12
 */
public class JsonEntityMapperTest {
    @Test
    public void test() {
        JdbcTemplate jt = new JdbcTemplate(createH2DS());
        jt.update("create table foo(id bigint, val varchar(255), da timestamp)");
        jt.update("insert into foo(id, val, da) values(1, '{\"foo\": 42, \"bar\": \"aaa\"}', '2012-01-01 01:23:45')");
        Map<String, Field> fieldsMap = CtzReflectionUtils.columnsFieldMap(TableBean.class);
        JsonEntityMapper<TableBean> mapper = new JsonEntityMapper<TableBean>(TableBean.class, fieldsMap, new LocalDateTimeFilter("da"));
        TableBean loaded = jt.queryForObject("select id, val, da from foo where id = 1", mapper);
        assertNotNull("Load fail", loaded);
        assertEquals("Simple map fail", 1L, loaded.getId());
        assertEquals("LDT filter fail", new LocalDateTime(2012, 1, 1, 1, 23, 45), loaded.getLdt());
        assertNotNull("Gson fail", loaded.getFooJsonBean());
        assertEquals("Gson int field fail", 42, loaded.getFooJsonBean().getFoo());
        assertEquals("Gson string field fail", "aaa", loaded.getFooJsonBean().getBar());
    }

    private DataSource createH2DS() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:foo");
        return ds;
    }

    @Entity
    private static class TableBean {
        @Id
        @Column
        private long id;
        @Column(name = "val")
        private FooJsonBean fooJsonBean;
        @Column(name = "da")
        @Type(type = "ru.concerteza.util.date.hibernate.PersistentLocalDateTime")
        private LocalDateTime ldt;

        public long getId() {
            return id;
        }

        public FooJsonBean getFooJsonBean() {
            return fooJsonBean;
        }

        public LocalDateTime getLdt() {
            return ldt;
        }
    }

    private static class FooJsonBean {
        private int foo;
        private String bar;

        public int getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }
    }
}
