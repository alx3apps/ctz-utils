package ru.concerteza.util.freemarker;

import org.springframework.core.io.Resource;
import ru.concerteza.util.io.CtzIOUtils;

import java.io.IOException;
import java.io.InputStream;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 5/2/12
 */
public class ResourceTemplateProvider implements TemplateProvider {
    @Override
    public InputStream loadTemplate(String path) throws IOException {
        Resource res = CtzIOUtils.RESOURCE_LOADER.getResource(path);
        if(!res.exists()) throw new IOException(format("Cannot load template for url: '{}'", path));
        return res.getInputStream();
    }
}
