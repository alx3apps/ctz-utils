package ru.concerteza.util.freemarker;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: alexey
 * Date: 11/19/11
 */
public interface TemplateProvider {
    InputStream loadTemplate(String path) throws IOException;
}
