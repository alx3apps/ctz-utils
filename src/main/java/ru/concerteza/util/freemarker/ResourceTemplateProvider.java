package ru.concerteza.util.freemarker;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.concerteza.util.io.CtzIOUtils;

import java.io.IOException;
import java.io.InputStream;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 5/2/12
 */
public class ResourceTemplateProvider implements TemplateProvider {

    private ResourceLoader loader = CtzIOUtils.RESOURCE_LOADER;

    public ResourceTemplateProvider() {
    }

    public ResourceTemplateProvider(ResourceLoader loader) {
        this.loader = loader;
    }

    @Override
    public InputStream loadTemplate(String path) throws IOException {
        Resource res = loader.getResource(path);
        if(!res.exists()) throw new IOException(format("Cannot load template for url: '{}'", path));
        return res.getInputStream();
    }
}
