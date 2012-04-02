package ru.concerteza.util.freemarker;

import freemarker.cache.TemplateCache;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.CtzConstants;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * User: alexey
 * Date: 10/21/11
 */

// preconfigured for Spring by default
public class FreemarkerEngine {
    private Configuration configuration;
    private TemplateProvider templateProvider;
    // default values for spring
    private String templateEncoding = CtzConstants.UTF8;
    private boolean useTemplatesCache = false;

    // spring friendly, dont use directly
    public FreemarkerEngine() {
    }

    // use this or next for no DI setup
    public FreemarkerEngine(Configuration configuration, TemplateProvider templateProvider) {
        this(configuration, templateProvider, CtzConstants.UTF8, false);
    }

    public FreemarkerEngine(Configuration configuration, TemplateProvider templateProvider, String templateEncoding, boolean useTemplatesCache) {
        this.configuration = configuration;
        this.templateEncoding = templateEncoding;
        this.templateProvider = templateProvider;
        this.useTemplatesCache = useTemplatesCache;
        try {
            postConstruct();
        } catch (NoSuchFieldException e) {
            throw new UnhandledException(e);
        } catch (IllegalAccessException e) {
            throw new UnhandledException(e);
        }
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setTemplateEncoding(String templateEncoding) {
        this.templateEncoding = templateEncoding;
    }

    public void setTemplateProvider(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    // spring init
    private void postConstruct() throws NoSuchFieldException, IllegalAccessException {
        final TemplateCache cache;
        if(useTemplatesCache) {
            cache = new MemoryTemplateCache(configuration, templateEncoding, templateProvider);
        } else {
            cache = new ProxyTemplateCache(configuration, templateEncoding, templateProvider);
        }
        // hack here to workaround FM name normalizing (it breaks abs. path keys)
        Field cacheField = Configuration.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        cacheField.set(configuration, cache);
    }

    public String process(String path, Map<String, ?> params) {
        StringWriter writer = new StringWriter();
        process(path, params, writer);
        return writer.toString();
    }

    public void process(String path, Map<String, ?> params, Writer writer) {
        try {
            Template ftl = configuration.getTemplate(path);
            ftl.process(params, writer);
        } catch (TemplateException e) {
            throw new UnhandledException(e);
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }
}
