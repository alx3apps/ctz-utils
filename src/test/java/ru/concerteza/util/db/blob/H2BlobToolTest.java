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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/h2-blob-tool-test-ctx.xml"})
public class H2BlobToolTest {

    @Inject
    private BlobTestService service;
    @Inject
    private DataSource dataSource;

    @Before
    public void prepareBlobsTable() {
        JdbcTemplate jt = new JdbcTemplate(dataSource);
        jt.update("drop sequence if exists blob_storage_id_seq");
        jt.update("drop table if exists blob_storage");
        jt.update("create sequence blob_storage_id_seq");
        jt.update("create table blob_storage (id bigint primary key, data blob);");
    }

    @Test
    public void test() throws IOException {
        long id = service.create();
        service.read(id);
        service.detach(id);
        service.delete(id);
    }

    @Test(expected = BlobException.class)
    public void testDelete() throws IOException {
        long id = service.create();
        service.delete(id);
        service.read(id);
    }
}


