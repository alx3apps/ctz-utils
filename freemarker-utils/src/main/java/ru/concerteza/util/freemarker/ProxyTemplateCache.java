package ru.concerteza.util.freemarker;

import freemarker.cache.TemplateCache;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;

import java.io.*;
import java.util.Locale;

/**
 * User: alexey
 * Date: 10/21/11
 */

class ProxyTemplateCache extends TemplateCache {

    protected final String encoding;
    private final Configuration config;
    private final TemplateProvider provider;

    protected ProxyTemplateCache(Configuration config, String encoding, TemplateProvider provider) {
        super(new DummyTemplateLoader());
        this.encoding = encoding;
        this.config = config;
        this.provider = provider;
    }

    // walking past FM loading inner kitchen
    @Override
    public Template getTemplate(String name, Locale locale, String dummy, boolean parse) throws IOException {
        Reader reader = null;
        try {
            InputStream is = provider.loadTemplate(name);
            reader = new InputStreamReader(is, encoding);
            return new Template(name, reader, config, encoding);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private static class DummyTemplateLoader implements TemplateLoader {
        @Override
        public Object findTemplateSource(String name) throws IOException {
            throw new NotImplementedException();
        }

        @Override
        public long getLastModified(Object templateSource) {
            throw new NotImplementedException();
        }

        @Override
        public Reader getReader(Object templateSource, String encoding) throws IOException {
            throw new NotImplementedException();
        }

        @Override
        public void closeTemplateSource(Object templateSource) throws IOException {
            throw new NotImplementedException();
        }
    }
}

