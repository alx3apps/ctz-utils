package ru.concerteza.util.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.concerteza.util.io.CtzResourceUtils;
import ru.concerteza.util.io.RuntimeIOException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static freemarker.ext.beans.BeansWrapper.EXPOSE_PROPERTIES_ONLY;
import static ru.concerteza.util.string.CtzConstants.UTF8;
import static ru.concerteza.util.string.CtzFormatUtils.format;
import static ru.concerteza.util.io.CtzResourceUtils.path;

/**
 * <a href="/freemarker.org/">Freemarker Template Engine</a> frontend for generic (non servlet) template formatting usage.
 * Spring-config and spring-resources friendly, different default settings, NIH cache implementation (disabled by default).
 * Default config values, that are different from freemarker's <a href="http://freemarker.org/docs/api/freemarker/template/Configuration.html">Configuration</a>
 * and <a href="http://freemarker.org/docs/api/freemarker/core/Configurable.html">Configurable</a> values:
 * <ul>
 *     <li>templateEncoding: {@code UTF-8}</li>
 *     <li>localizedLookup: {@code false}</li>
 *     <li>tagSyntax: {@code SQUARE_BRACKET_TAG_SYNTAX}</li>
 *     <li>templateUpdateDelay: {@code Integer.MAX_VALUE}</li>
 *     <li>numberFormat: {@code computer}</li>
 *     <li>BeansWrapper.exposureLevel: {@code EXPOSE_PROPERTIES_ONLY}</li>
 * </ul>
 * See usage examples in {@link FreemarkerEngineTest}
 *
 * @author alexey,
 * Date: 10/21/11
 */
public class FreemarkerEngine extends Configuration {
    private ResourceLoader resourceLoader = CtzResourceUtils.RESOURCE_LOADER;
    private String templateEncoding = UTF8;

    private Map<String, Template> templateCache;
    private final Object templateCacheLock = new Object();

    /**
     * Spring friendly constructor
     */
    public FreemarkerEngine() {
        // change defaults
        this.setLocalizedLookup(false);
        this.setTagSyntax(SQUARE_BRACKET_TAG_SYNTAX);
        this.setTemplateUpdateDelay(Integer.MAX_VALUE);
        this.setNumberFormat("computer");
        BeansWrapper bw = (BeansWrapper) this.getObjectWrapper();
        bw.setExposureLevel(EXPOSE_PROPERTIES_ONLY);
    }

    /**
     * Frontend method
     * @param path spring's <a href="http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/resources.html">resource</a> path
     * @param params template's model root object
     * @return rendered template
     * @throws RuntimeIOException on IO error
     */
    public String process(String path, Object params) throws RuntimeIOException {
        StringWriter writer = new StringWriter();
        process(path, params, writer);
        return writer.toString();
    }

    /**
     * Frontend method
     * @param path spring's <a href="http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/resources.html">resource</a> path
     * @param params template's model root object
     * @param output {@link OutputStream} to render template into
     * @param outputEncoding rendering results encoding
     * @throws RuntimeIOException on IO error
     */
    public void process(String path, Object params, OutputStream output, String outputEncoding) throws RuntimeIOException {
        try {
            Writer writer = new OutputStreamWriter(output, outputEncoding);
            process(path, params, writer);
        } catch(UnsupportedEncodingException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Frontend method
     * @param path spring's <a href="http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/resources.html">resource</a> path
     * @param params template's model root object
     * @param writer {@link Writer} to render template into
     * @throws RuntimeIOException on IO error
     */
    public void process(String path, Object params, Writer writer) throws RuntimeIOException {
        Resource resource = resourceLoader.getResource(path);
        process(resource, params, writer);
    }

    /**
     * Frontend method
     * @param resource spring's <a href="http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/resources.html">resource</a>
     * @param params template's model root object
     * @param writer {@link Writer} to render template into
     * @throws RuntimeIOException on IO error
     */
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

    /**
     * Frontend method, uses "UTF-8" encoding for output, template cache not used for this method
     * @param input template's body {@link InputStream}
     * @param params templates model root object
     * @param output {@link OutputStream} to render template into
     * @throws RuntimeIOException on IO error
     */
    public void process(InputStream input, Object params, OutputStream output) throws RuntimeIOException {
        process(input, params, output, UTF8);
    }

    /**
     * Frontend method, template cache not used for this method
     * @param input template's body {@link InputStream}
     * @param params template's model root object
     * @param output {@link OutputStream} to render template into
     * @param outputEncoding rendering results encoding
     * @throws RuntimeIOException on IO error
     */
    public void process(InputStream input, Object params, OutputStream output, String outputEncoding) throws RuntimeIOException {
        try {
            Reader reader = new InputStreamReader(input, templateEncoding);
            Writer writer = new OutputStreamWriter(output, outputEncoding);
            process(reader, params, writer);
        } catch (UnsupportedEncodingException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Frontend method, template cache not used for this method
     * @param reader template's body {@link Reader}
     * @param params template's model root object
     * @param writer {@link Writer} to render template into
     * @throws RuntimeIOException on IO error
     */
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

    /**
     *  templateEncoding setter, use for non UTF-8 templates
     *  @param templateEncoding encoding use in templates parsing,
     *  must match {@code #ftl encoding} attribute in template if specified
     */
    public void setTemplateEncoding(String templateEncoding) {
        this.templateEncoding = templateEncoding;
    }

    /**
     *  Enables caching, all parsed templates will be cached im-memory, false by default
     *  @param useTemplatesCache use cache flag
     */
    public void setUseTemplatesCache(boolean useTemplatesCache) {
        templateCache = useTemplatesCache ? new HashMap<String, Template>() : null;
    }

    /**
     * Exposure level setter
     * @param level see <a href="http://freemarker.sourceforge.net/docs/api/freemarker/ext/beans/BeansWrapper.html#EXPOSE_ALL">BeansWrapper API</a>
     */
    public void setExposureLevel(int level) {
        BeansWrapper bw = (BeansWrapper) this.getObjectWrapper();
        bw.setExposureLevel(level);
    }

    /**
     * Spring's <a href="http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/core/io/ResourceLoader.html">ResourceLoader</a>
     * setter, {@link CtzResourceUtils#RESOURCE_LOADER} by default, use another resourceLoader on complex classloader config
     * @param resourceLoader Spring's <a href="http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/core/io/ResourceLoader.html">ResourceLoader</a>
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
