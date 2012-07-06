package ru.concerteza.util.db.springjdbc.named;

import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.concerteza.util.db.csv.CsvDataSource;

import javax.inject.Named;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static ru.concerteza.util.CtzConstants.UTF8;

/**
 * User: alexey
 * Date: 7/5/12
 */
public class NamedConstructorMapperTest {

    @Test
    public void testSingle() throws Exception {
        Resource resource = new ByteArrayResource("bar|baz\n41|42".getBytes(UTF8));
        CsvDataSource ds = new CsvDataSource(resource, "|", UTF8);
        NamedParameterJdbcTemplate jt = new NamedParameterJdbcTemplate(ds);
        NamedConstructorMapper<Foo> mapper = NamedConstructorMapper.forClass(Foo.class);
        Foo foo = jt.getJdbcOperations().queryForObject("some sql", mapper);
        assertNotNull("Creation fail", foo);
        assertEquals("Data fail", "41", foo.bar);
        assertEquals("Data fail", "42", foo.baz);
    }

    @Test
    public void testSubclasses() throws UnsupportedEncodingException {
        Resource resource = new ByteArrayResource("disc|foo|bar|baz\nfirst|40|41|NULL\nsecond|43|NULL|45".getBytes(UTF8));
        CsvDataSource ds = new CsvDataSource(resource, "|", UTF8);
        NamedParameterJdbcTemplate jt = new NamedParameterJdbcTemplate(ds);
        NamedConstructorMapper<Parent> mapper = NamedConstructorMapper.<Parent>builder("disc")
                .addSubclass("first", First.class)
                .addSubclass("second", Second.class)
                .build();
        List<Parent> list = jt.getJdbcOperations().query("some sql", mapper);
        assertEquals("Size fail", 2, list.size());
        assertNotNull("Instantiation fail", list.get(0));
        assertNotNull("Instantiation fail", list.get(1));
        assertTrue("Subclass fail", list.get(0) instanceof First);
        assertTrue("Subclass fail", list.get(1) instanceof Second);
        assertEquals("Data fail", "40", list.get(0).foo);
        assertEquals("Data fail", "43", list.get(1).foo);
        assertEquals("Data fail", "41", ((First) list.get(0)).bar);
        assertEquals("Data fail", "45", ((Second) list.get(1)).baz);
    }

    private static class Foo {
        private final String bar;
        private final String baz;

        private Foo(@Named("bar") String bar, @Named("baz") String baz) {
            this.bar = bar;
            this.baz = baz;
        }
    }

    private static class Parent {
        protected final String foo;

        private Parent(String foo) {
            this.foo = foo;
        }
    }

    private static class First extends Parent {
        private final String bar;

        private First(@Named("foo") String foo, @Named("bar") String bar) {
            super(foo);
            this.bar = bar;
        }
    }

    private static class Second extends Parent {
        private final String baz;

        private Second(@Named("foo") String foo, @Named("baz") String baz) {
            super(foo);
            this.baz = baz;
        }
    }
}