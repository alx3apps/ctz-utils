package ru.concerteza.util.freemarker;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: alexey
 * Date: 4/2/12
 */

// Spring specific loader, won't work without spring
public class ResourceTemplateProvider implements TemplateProvider, ApplicationContextAware {
    private ResourceLoader loader;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.loader = applicationContext;
    }

    @Override
    public InputStream loadTemplate(String path) throws IOException {
        return loader.getResource(path).getInputStream();
    }
}
