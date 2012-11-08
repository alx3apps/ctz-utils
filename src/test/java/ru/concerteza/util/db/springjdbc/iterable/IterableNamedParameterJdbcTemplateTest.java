package ru.concerteza.util.db.springjdbc.iterable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: alexkasko
 * Date: 11/8/12
 */
public class IterableNamedParameterJdbcTemplateTest {
    private static final IterableNamedParameterJdbcTemplate jt;

    static {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:");
        jt = new IterableNamedParameterJdbcTemplate(ds);
    }

    @Test
    public void test() {
        jt.getJdbcOperations().update("create table foo(val varchar(255))");
        jt.getJdbcOperations().update("insert into foo(val) values('foo')");
        jt.getJdbcOperations().update("insert into foo(val) values('bar')");
        jt.getJdbcOperations().update("insert into foo(val) values('baz')");
        // test static sql
        CloseableIterator<String> iterStatic = jt.getIterableJdbcOperations().queryForIter(
                        "select val from foo where val like 'b%' order by val",
                        String.class);
        validateIter(iterStatic);
        // test prepared statement
        CloseableIterator<String> iterPrepared = jt.queryForIter(
                "select val from foo where val like :val order by val",
                ImmutableMap.of("val", "b%"), String.class);
        validateIter(iterPrepared);
    }

    private void validateIter(CloseableIterator<String> iter) {
        assertFalse("Open fail", iter.isClosed());
        List<String> list = ImmutableList.copyOf(iter);
        assertTrue("Close fail", iter.isClosed());
        assertEquals("Size fail", 2, list.size());
        assertEquals("Data fail", "bar", list.get(0));
        assertEquals("Data fail", "baz", list.get(1));
    }
}
