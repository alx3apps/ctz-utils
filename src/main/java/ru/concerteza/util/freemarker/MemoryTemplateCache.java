package ru.concerteza.util.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: alexey
 * Date: 4/2/12
 */
class MemoryTemplateCache extends ProxyTemplateCache {

    private final Map<String, Template> cache = new HashMap<String, Template>();

    MemoryTemplateCache(Configuration config, String encoding, TemplateProvider provider) {
        super(config, encoding, provider);
    }

    @Override
    public synchronized Template getTemplate(String name, Locale locale, String dummy, boolean parse) throws IOException {
        Template cached = cache.get(name);
        final Template res;
        if(null == cached) {
            res = super.getTemplate(name, locale, dummy, parse);
            cache.put(name, res);
        } else {
            res = cached;
        }
        return res;
    }
}
