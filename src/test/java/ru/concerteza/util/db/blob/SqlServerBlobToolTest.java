package ru.concerteza.util.db.blob;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 4/25/12
 */

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:/sqlserver-blob-tool-test-ctx.xml"})
// create table blob_storage_id_seq_table(id bigint identity)
// create procedure blob_storage_id_seq_fun as begin insert into blob_storage_id_seq_table default values;	select ident_current('blob_storage_id_seq_table'); end;
// create table blob_storage(id bigint primary key, data image);
public class SqlServerBlobToolTest {

    @Inject
    private BlobTestService service;
    @Inject
    private DataSource dataSource;
//    @Inject
//    private BlobTestLargeService largeService;
//    @Value("${ctzutils.blob.large_file.path}")
//    private File largeFile;
//    @Value("${ctzutils.blob.large_file.sha1}")
//    private String largeFileSha1;

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

//    cannot make work, always got this on big file
//    http://sumitpal.wordpress.com/2010/08/26/different-things-i-tried-to-solve-sql-error-io-error-connection-reset/
//    @Test
//    public void testLarge() throws IOException {
//        long id = largeService.create(largeFile);
//        String sha1 = largeService.readSha1(id);
//        largeService.delete(id);
//        assertEquals(largeFileSha1, sha1);
//    }
}


