package ru.concerteza.util.freemarker;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 11/19/11
 */
public class FreemarkerEngineTest {

    @Test
    public void testClasspath() {
//        Map<String, String> params = ImmutableMap.of("foo", "bar");
        String res = createEngine().process("/FreemarkerEngineTest.ftl", new Params("bar"));
        assertEquals("Hello bar", res);
    }

    private FreemarkerEngine createEngine() {
        TemplateProvider provider = new ClassPathTemplateProvider();
        FreemarkerEngine res = new FreemarkerEngine();
        res.setTemplateProvider(provider);
        return res;
    }

    private class ClassPathTemplateProvider implements TemplateProvider {
        @Override
        public InputStream loadTemplate(String path) throws IOException {
            return ClassPathTemplateProvider.class.getResourceAsStream(path);
        }
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
