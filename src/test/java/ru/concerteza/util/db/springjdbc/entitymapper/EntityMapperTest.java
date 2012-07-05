package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.concerteza.util.db.springjdbc.entitymapper.filters.ColumnsToLowerFilter;
import ru.concerteza.util.db.springjdbc.entitymapper.filters.JsonFilter;
import ru.concerteza.util.db.springjdbc.entitymapper.filters.LocalDateTimeFilter;

import javax.persistence.*;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.concerteza.util.date.hibernate.PersistentLocalDateTime.LOCAL_DATE_TIME_TYPE;

/**
 * User: alexey
 * Date: 4/29/12
 */
public class EntityMapperTest {
    private static final JdbcTemplate jt;

    static {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:EntityMapperTest1");
        jt = new JdbcTemplate(ds);
    }

    @Test
    public void testSimple() {
        jt.update("create table foo(foo_id bigint, val varchar(255))");
        jt.update("insert into foo(foo_id, val) values(1, 'foo')");
        RowMapper<Foo> mapper = EntityMapper.forClass(Foo.class);
        Foo foo = jt.queryForObject("select foo_id, val from foo where foo_id = 1", mapper);
        assertNotNull("Load fail", foo);
        assertEquals("Field fail", 1L, foo.id);
        assertEquals("Field fail", "foo", foo.val);
        jt.update("drop table foo");
    }

    @Test
    public void testFilters() {
        jt.update("create table foo(id bigint, val varchar(255), da timestamp)");
        jt.update("insert into foo(id, val, da) values(1, '{\"foo\": 42, \"bar\": \"aaa\"}', '2012-01-01 01:23:45')");
        RowMapper<TableBean> mapper = EntityMapper.forClass(TableBean.class,
                new ColumnsToLowerFilter(),
                new LocalDateTimeFilter("da"),
                new JsonFilter(new Gson(), "val", FooJsonBean.class));
        TableBean loaded = jt.queryForObject("select * from foo where id = 1", mapper);
        assertNotNull("Load fail", loaded);
        assertEquals("Simple map fail", 1L, loaded.id);
        assertEquals("LDT filter fail", new LocalDateTime(2012, 1, 1, 1, 23, 45), loaded.ldt);
        assertNotNull("Gson fail", loaded.fooJsonBean);
        assertEquals("Gson int field fail", 42, loaded.fooJsonBean.foo);
        assertEquals("Gson string field fail", "aaa", loaded.fooJsonBean.bar);
        jt.update("drop table foo");
    }

    @Test
    // todo test subclasses filters
    public void testSubclasses() {
        jt.update("create table bar(id bigint, disc varchar(255), first_child_field varchar(255), second_child_field varchar(255))");
        jt.update("insert into bar(id, disc, first_child_field, second_child_field) values(1, 'first', 'foo', null)");
        jt.update("insert into bar(id, disc, first_child_field, second_child_field) values(2, 'second', null, 'bar')");
        RowMapper<Parent> mapper = EntityMapper.builder(new ChildChooser()).build();
        Parent firstLoaded = jt.queryForObject("select * from bar where id = 1", mapper);
        assertNotNull("Load fail", firstLoaded);
        assertEquals("Inheritance fail", FirstChild.class, firstLoaded.getClass());
        FirstChild first = (FirstChild) firstLoaded;
        assertEquals("Parent field fail", 1L, first.id);
        assertEquals("Child field fail", "foo", first.firstChildField);
        Parent secondLoaded = jt.queryForObject("select * from bar where id = 2", mapper);
        assertNotNull("Load fail", secondLoaded);
        assertEquals("Inheritance fail", SecondChild.class, secondLoaded.getClass());
        SecondChild second = (SecondChild) secondLoaded;
        assertEquals("Parent field fail", 2L, second.id);
        assertEquals("Child field fail", "bar", second.secondChildField);
        jt.update("drop table bar");
    }

    private DataSource createH2DS() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:EntityMapperTest2");
        return ds;
    }

    @Entity
    @Table(name = "foo")
    private static class Foo {
        @Id
        @Column(name = "foo_id")
        private long id;
        @Column
        private String val;
    }

    @Entity
    @Table(name = "foo")
    private static class TableBean {
        @Id
        @Column
        private long id;
        @Column(name = "val")
        private FooJsonBean fooJsonBean;
        @Column(name = "da")
        @Type(type = LOCAL_DATE_TIME_TYPE)
        private LocalDateTime ldt;
    }

    private static class FooJsonBean {
        private int foo;
        private String bar;
    }

    @Entity
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(
        name = "disc",
        discriminatorType = DiscriminatorType.STRING
    )
    @Table(name = "bar")
    private static abstract class Parent {
        @Id
        @Column
        protected long id;
    }

    @Entity
    @DiscriminatorValue("first")
    private static class FirstChild extends Parent {
        @Column(name = "first_child_field")
        private String firstChildField;
    }

    @Entity
    @DiscriminatorValue("second")
    private static class SecondChild extends Parent {
        @Column(name = "second_child_field")
        private String secondChildField;
    }

    private static class ChildChooser implements EntityChooser<Parent> {

        @Override
        public Set<Class<? extends Parent>> subclasses() {
            return ImmutableSet.of(FirstChild.class, SecondChild.class);
        }

        @Override
        public Class<? extends Parent> choose(Map<String, ?> dataMap) {
            String disc = (String) dataMap.get("disc");
            if("first".equals(disc)) return FirstChild.class;
            else if("second".equals(disc)) return SecondChild.class;
            throw new IllegalStateException(disc);
        }
    }
}
