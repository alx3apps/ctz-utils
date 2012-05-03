package ru.concerteza.util.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.CtzConstants;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static freemarker.ext.beans.BeansWrapper.EXPOSE_PROPERTIES_ONLY;

/**
 * User: alexey
 * Date: 10/21/11
 */

public class FreemarkerEngine extends Configuration {
    private TemplateProvider templateProvider = new ResourceTemplateProvider();
    private String templateEncoding = CtzConstants.UTF8;

    private Map<String, Template> templateCache;
    private final Object templateCacheLock = new Object();

    public FreemarkerEngine() {
        // change defaults
        this.setLocalizedLookup(false);
        this.setTagSyntax(SQUARE_BRACKET_TAG_SYNTAX);
        this.setTemplateUpdateDelay(Integer.MAX_VALUE);
        this.setNumberFormat("computer");
        BeansWrapper bw = (BeansWrapper) this.getObjectWrapper();
        bw.setExposureLevel(EXPOSE_PROPERTIES_ONLY);
    }

    // use for non UTF-8 templates
    public void setTemplateEncoding(String templateEncoding) {
        this.templateEncoding = templateEncoding;
    }

    // use for custom provider, spring resources URL's used by default
    public void setTemplateProvider(TemplateProvider templateProvider) {
        this.templateProvider = templateProvider;
    }

    // all templates will be cached im-memory, false by default
    public void setUseTemplatesCache(boolean useTemplatesCache) {
        templateCache = useTemplatesCache ? new HashMap<String, Template>() : null;
    }

    public void setExposureLevel(int level) {
        BeansWrapper bw = (BeansWrapper) this.getObjectWrapper();
        bw.setExposureLevel(level);
    }

    public String process(String path, Object params) {
        StringWriter writer = new StringWriter();
        process(path, params, writer);
        return writer.toString();
    }

    public void process(String path, Object params, Writer writer) {
        try {
            Template ftl = findTemplate(path);
            ftl.process(params, writer);
        } catch (TemplateException e) {
            throw new UnhandledException(e);
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    private Template findTemplate(String path) throws IOException {
        if (null == templateCache) return loadTemplate(path);
        synchronized (templateCacheLock) {
            Template cached = templateCache.get(path);
            final Template res;
            if (null == cached) {
                res = loadTemplate(path);
                templateCache.put(path, res);
            } else {
                res = cached;
            }
            return res;
        }
    }

    private Template loadTemplate(String path) throws IOException {
        Reader reader = null;
        try {
            InputStream is = templateProvider.loadTemplate(path);
            reader = new InputStreamReader(is, templateEncoding);
            return new Template(path, reader, this, templateEncoding);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
