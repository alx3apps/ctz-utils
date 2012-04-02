package ru.concerteza.util.freemarker;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import org.junit.Test;
import ru.concerteza.util.CtzConstants;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * User: alexey
 * Date: 11/19/11
 */
public class FreemarkerEngineTest {

    @Test
    public void testClasspath() {
        Map<String, String> params = ImmutableMap.of("foo", "bar");
        String res = createEngine().process("/FreemarkerEngineTest.ftl", params);
        assertEquals("Hello bar", res);
    }

    private FreemarkerEngine createEngine() {
        Configuration conf = new Configuration();
        conf.setLocalizedLookup(false);
        conf.setDefaultEncoding(CtzConstants.UTF8);
        conf.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        conf.setTemplateUpdateDelay(Integer.MAX_VALUE);
        conf.setNumberFormat("computer");
        DefaultObjectWrapper objectWrapper = new DefaultObjectWrapper();
        objectWrapper.setExposeFields(true);
        conf.setObjectWrapper(objectWrapper);
        TemplateProvider provider = new ClassPathTemplateProvider();
        return new FreemarkerEngine(conf, provider);
    }
}
