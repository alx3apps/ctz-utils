package ru.concerteza.util.db.blob;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;
import ru.concerteza.util.db.blob.tool.BlobTool;

import javax.inject.Inject;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.apache.commons.lang.RandomStringUtils.random;

/**
 * User: alexey
 * Date: 4/25/12
 */

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:/postgre-blob-tool-test-ctx.xml"})
public class PostgreBlobToolTest {

    @Inject
    private BlobTestService service;

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


