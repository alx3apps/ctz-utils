//package ru.concerteza.util.db.csv;
//
//import org.junit.Test;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.sql.DataSource;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * User: alexey
// * Date: 6/29/12
// */
//public class CsvDataSourceTest {
//    @Test
//    public void test() {
//        DataSource ds = new CsvDataSource("/data.csv", ";\t");
//        JdbcTemplate jt = new JdbcTemplate(ds);
//        List<Map<String, Object>> res = jt.queryForList("select something");
//        assertEquals("Row count fail", 3, res.size());
//        assertEquals("Metadata fail", "foo", res.get(0).keySet().toArray()[0]);
//        assertEquals("Metadata fail", "bar", res.get(1).keySet().toArray()[1]);
//        assertEquals("Metadata fail", "baz", res.get(2).keySet().toArray()[2]);
//        assertEquals("Data fail", "foo1", res.get(0).get("foo"));
//        assertEquals("Data fail", "bar2", res.get(1).get("bar"));
//        assertEquals("Data fail", "baz3", res.get(2).get("baz"));
//    }
//}
