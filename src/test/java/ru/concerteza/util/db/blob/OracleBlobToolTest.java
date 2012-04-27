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
import java.sql.SQLException;

/**
 * User: alexey
 * Date: 4/25/12
 */


// create sequence blob_storage_id_seq;
// create table blob_storage (id int primary key, data blob);
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:/oracle-blob-tool-test-ctx.xml"})
public class OracleBlobToolTest {

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


