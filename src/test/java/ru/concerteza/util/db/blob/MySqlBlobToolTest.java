package ru.concerteza.util.db.blob;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * User: alexey
 * Date: 4/25/12
 */

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:/mysql-blob-tool-test-ctx.xml"})
// Note: there are better ways to simulate sequences in MySQL
// create table blob_storage_id_seq_table(id bigint primary key auto_increment, dummy int);
// delimiter |
// create function blob_storage_id_seq_function() returns bigint begin declare res bigint; insert into blob_storage_id_seq_table(dummy) values(42); set res = last_insert_id(); return res; end |
// delimiter ;
// create table if not exists blob_storage(id bigint primary key, data blob);
public class MySqlBlobToolTest {

    @Inject
    private BlobTestService service;
    @Inject
    private DataSource dataSource;

    @Test
    public void dummy() {
//      test is disable by default
    }

//    @Test
    public void test() throws IOException {
        long id = service.create();
        service.read(id);
        service.detach(id);
        service.delete(id);
    }

//    @Test(expected = BlobException.class)
    public void testDelete() throws IOException {
        long id = service.create();
        service.delete(id);
        service.read(id);
    }
}


