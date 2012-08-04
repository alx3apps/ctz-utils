package ru.concerteza.util.freemarker;

import org.junit.Test;
import org.springframework.core.io.Resource;
import ru.concerteza.util.string.CtzConstants;
import ru.concerteza.util.io.CtzResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 11/19/11
 */
public class FreemarkerEngineTest {

    @Test
    public void test() throws IOException {
        FreemarkerEngine engine = new FreemarkerEngine();
        Params params = new Params("bar");
        String result = engine.process("classpath:/FreemarkerEngineTest.ftl", params);
        assertEquals("Hello bar", result);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Resource res = CtzResourceUtils.RESOURCE_LOADER.getResource("classpath:/FreemarkerEngineTest.ftl");
        engine.process(res.getInputStream(), params, baos);
        String streams = new String(baos.toByteArray(), CtzConstants.UTF8);
        assertEquals("Hello bar", streams);
    }

    public class Params {
        private final String foo;

        private Params(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }
    }

}
