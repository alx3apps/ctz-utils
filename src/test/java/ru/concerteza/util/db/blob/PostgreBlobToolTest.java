package ru.concerteza.util.db.blob;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 4/25/12
 */

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:/postgre-blob-tool-test-ctx.xml"})
public class PostgreBlobToolTest {

    @Inject
    private BlobTestService service;
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

//    with 1GB file
//    Total time: 12:06.717s
//    @Test
//    public void testLarge() throws IOException {
//        long id = largeService.create(largeFile);
//        String sha1 = largeService.readSha1(id);
//        largeService.delete(id);
//        assertEquals(largeFileSha1, sha1);
//    }
}


