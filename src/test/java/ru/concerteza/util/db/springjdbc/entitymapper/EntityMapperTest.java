package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.*;
import java.util.*;

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
        ds.setUrl("jdbc:h2:mem:");
        jt = new JdbcTemplate(ds);
    }

    private EntityMapper<Foo> simpleMapper() {
        return EntityMapper.forClass(Foo.class);
    }

    private void checkSimple(Foo foo) {
        assertNotNull("Load fail", foo);
        assertEquals("Field fail", 1L, foo.id);
        assertEquals("Field fail", "foo", foo.val);
    }

    @Test
    public void testSimpleMap() {
        Map<String,?> fooMap = ImmutableMap.of("foo_id", 1L, "val", "foo");
        Foo foo = simpleMapper().map(fooMap);
        checkSimple(foo);
    }

    @Test
    public void testSimpleRs() {
        jt.update("create table foo(foo_id bigint, val varchar(255))");
        jt.update("insert into foo(foo_id, val) values(1, 'foo')");
        Foo foo = jt.queryForObject("select foo_id, val from foo where foo_id = 1", simpleMapper());
        checkSimple(foo);
        jt.update("drop table foo");
    }

    private EntityMapper<TableBean> filtersMapper() {
        return EntityMapper.forClass(TableBean.class,
                EntityFilters.columnsToLower(),
                EntityFilters.toLocalDateTime("da"),
                EntityFilters.fromJson(new Gson(), "val", FooJsonBean.class));
    }

    private void checkFilters(TableBean tableBean) {
        assertNotNull("Load fail", tableBean);
        assertEquals("Simple map fail", 1L, tableBean.id);
        assertEquals("LDT filter fail", new LocalDateTime(2012, 1, 1, 1, 23, 45), tableBean.ldt);
        assertNotNull("Gson fail", tableBean.fooJsonBean);
        assertEquals("Gson int field fail", 42, tableBean.fooJsonBean.foo);
        assertEquals("Gson string field fail", "aaa", tableBean.fooJsonBean.bar);
    }

    @Test
    public void testFiltersMap() {
        Date date = new LocalDateTime(2012, 1, 1, 1, 23, 45).toDate();
        Map<String,?> tableBeanMap = ImmutableMap.of("id", 1L, "val", "{\"foo\": 42, \"bar\": \"aaa\"}", "da", date);
        TableBean tableBean = filtersMapper().map(tableBeanMap);
        checkFilters(tableBean);
    }

    @Test
    public void testFiltersRs() {
        jt.update("create table foo(id bigint, val varchar(255), da timestamp)");
        jt.update("insert into foo(id, val, da) values(1, '{\"foo\": 42, \"bar\": \"aaa\"}', '2012-01-01 01:23:45')");
        TableBean tableBean = jt.queryForObject("select * from foo where id = 1", filtersMapper());
        checkFilters(tableBean);
        jt.update("drop table foo");
    }

    private EntityMapper<Parent> subclassesMapper() {
        return EntityMapper.builder(new ChildChooser()).build();
    }

    private void checkSubclasses(Parent firstLoaded, Parent secondLoaded) {
        assertNotNull("Load fail", firstLoaded);
        assertEquals("Inheritance fail", FirstChild.class, firstLoaded.getClass());
        FirstChild first = (FirstChild) firstLoaded;
        assertEquals("Parent field fail", 1L, first.id);
        assertEquals("Child field fail", "foo", first.firstChildField);
        assertNotNull("Load fail", secondLoaded);
        assertEquals("Inheritance fail", SecondChild.class, secondLoaded.getClass());
        SecondChild second = (SecondChild) secondLoaded;
        assertEquals("Parent field fail", 2L, second.id);
        assertEquals("Child field fail", "bar", second.secondChildField);
    }

    @Test
    // todo test subclasses filters
    public void testSubclassesMap() {
        Map<String,?> firstLoadedMap = ImmutableMap.of("id", 1L, "disc", "first", "first_child_field", "foo");
        Map<String,?> secondLoadedMap = ImmutableMap.of("id", 2L, "disc", "second", "second_child_field", "bar");
        Parent firstLoaded = subclassesMapper().map(firstLoadedMap);
        Parent secondLoaded = subclassesMapper().map(secondLoadedMap);
        checkSubclasses(firstLoaded, secondLoaded);
    }

    @Test
    // todo test subclasses filters
    public void testSubclassesRs() {
        jt.update("create table bar(id bigint, disc varchar(255), first_child_field varchar(255), second_child_field varchar(255))");
        jt.update("insert into bar(id, disc, first_child_field, second_child_field) values(1, 'first', 'foo', null)");
        jt.update("insert into bar(id, disc, first_child_field, second_child_field) values(2, 'second', null, 'bar')");
        Parent firstLoaded = jt.queryForObject("select * from bar where id = 1", subclassesMapper());
        Parent secondLoaded = jt.queryForObject("select * from bar where id = 2", subclassesMapper());
        checkSubclasses(firstLoaded, secondLoaded);
        jt.update("drop table bar");
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
