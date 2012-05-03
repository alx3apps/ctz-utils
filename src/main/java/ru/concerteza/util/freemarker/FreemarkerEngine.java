package ru.concerteza.util.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.concerteza.util.CtzConstants;
import ru.concerteza.util.io.CtzResourceUtils;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static freemarker.ext.beans.BeansWrapper.EXPOSE_PROPERTIES_ONLY;
import static ru.concerteza.util.CtzConstants.UTF8;
import static ru.concerteza.util.CtzFormatUtils.format;
import static ru.concerteza.util.io.CtzResourceUtils.path;

/**
 * User: alexey
 * Date: 10/21/11
 */

public class FreemarkerEngine extends Configuration {
    private ResourceLoader resourceLoader = CtzResourceUtils.RESOURCE_LOADER;
    private String templateEncoding = UTF8;

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

    public String process(String path, Object params) throws RuntimeIOException {
        StringWriter writer = new StringWriter();
        process(path, params, writer);
        return writer.toString();
    }

    public void process(String path, Object params, Writer writer) throws RuntimeIOException {
        Resource resource = resourceLoader.getResource(path);
        process(resource, params, writer);
    }

    public void process(Resource resource, Object params, Writer writer) throws RuntimeIOException {
        try {
            Template ftl = findTemplate(resource);
            ftl.process(params, writer);
        } catch (TemplateException e) {
            throw new UnhandledException(e);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    // template cache not used for this method
    public void process(InputStream input, Object params, OutputStream output) throws RuntimeIOException {
        process(input, params, output, UTF8);
    }

    // template cache not used for this method
    public void process(InputStream input, Object params, OutputStream output, String outputEncoding) throws RuntimeIOException {
        try {
            Reader reader = new InputStreamReader(input, templateEncoding);
            Writer writer = new OutputStreamWriter(output, outputEncoding);
            process(reader, params, writer);
        } catch (UnsupportedEncodingException e) {
            throw new UnhandledException(e);
        }
    }

    // template cache not used for this method
    public void process(Reader reader, Object params, Writer writer) throws RuntimeIOException {
        try{
            Template ftl = new Template("reader_provided_template", reader, this, templateEncoding);
            ftl.process(params, writer);
        } catch (TemplateException e) {
            throw new UnhandledException(e);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private Template findTemplate(Resource resource) throws IOException {
        if (null == templateCache) return loadTemplate(resource);
        synchronized (templateCacheLock) {
            String key = path(resource);
            Template cached = templateCache.get(key);
            final Template template;
            if (null == cached) {
                template = loadTemplate(resource);
                templateCache.put(key, template);
            } else {
                template = cached;
            }
            return template;
        }
    }

    private Template loadTemplate(Resource resource) throws IOException {
        Reader reader = null;
        try {
            if(!resource.exists()) throw new IOException(format("Cannot load template for resource: '{}'", resource));
            InputStream is = resource.getInputStream();
            reader = new InputStreamReader(is, templateEncoding);
            return new Template(path(resource), reader, this, templateEncoding);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    // use for non UTF-8 templates
    public void setTemplateEncoding(String templateEncoding) {
        this.templateEncoding = templateEncoding;
    }

    // all templates will be cached im-memory, false by default
    public void setUseTemplatesCache(boolean useTemplatesCache) {
        templateCache = useTemplatesCache ? new HashMap<String, Template>() : null;
    }

    public void setExposureLevel(int level) {
        BeansWrapper bw = (BeansWrapper) this.getObjectWrapper();
        bw.setExposureLevel(level);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
