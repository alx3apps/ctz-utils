package ru.concerteza.util.freemarker;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: alexey
 * Date: 11/19/11
 */
public class ClassPathTemplateProvider implements TemplateProvider {

    @Override
    public InputStream loadTemplate(String path) throws IOException {
        return ClassPathTemplateProvider.class.getResourceAsStream(path);
    }
}
