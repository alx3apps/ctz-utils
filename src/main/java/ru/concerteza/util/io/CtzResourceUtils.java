package ru.concerteza.util.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static ru.concerteza.util.CtzFormatUtils.format;
import static ru.concerteza.util.io.CtzIOUtils.mkdirs;

/**
 * User: alexey
 * Date: 5/3/12
 */

public class CtzResourceUtils {
    // use only if you are sure about classloaders
    public static final PathMatchingResourcePatternResolver RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();
    public static final ResourceLoader RESOURCE_LOADER = RESOURCE_RESOLVER.getResourceLoader();

    public static void copyResourceToDir(String url, File dir) throws RuntimeIOException {
        mkdirs(dir);
        Resource re = RESOURCE_LOADER.getResource(url);
        File target = new File(dir, re.getFilename());
        copyResource(re, target);
    }

    public static void copyResource(String url, File target) throws RuntimeIOException {
        Resource re = RESOURCE_LOADER.getResource(url);
        copyResource(re, target);
    }

    public static void copyResource(Resource re, File target) throws RuntimeIOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            if(!re.exists()) throw new IOException(format("Cannot load resource: '{}'", re));
            is = re.getInputStream();
            os = FileUtils.openOutputStream(target);
            IOUtils.copyLarge(is, os);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    // will work only with 'classpath:' and 'file:' resources
    public static boolean isDirectory(Resource resource) throws RuntimeIOException {
        try {
            if (ClassPathResource.class.isAssignableFrom(resource.getClass())) {
                return 0 == resource.getFilename().length();
            } else {
                return resource.getFile().isDirectory();
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    // will work only with 'classpath:' and 'file:' resources
    public static String path(Resource resource) {
        try {
            if (ClassPathResource.class.isAssignableFrom(resource.getClass())) {
                ClassPathResource res = (ClassPathResource) resource;
                return "classpath:/" + res.getPath();
            } else {
                return "file:" + resource.getFile().getPath();
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}