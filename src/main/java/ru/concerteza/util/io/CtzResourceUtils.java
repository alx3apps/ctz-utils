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
import java.util.List;

import static ru.concerteza.util.string.CtzConstants.UTF8;
import static ru.concerteza.util.string.CtzFormatUtils.format;
import static ru.concerteza.util.io.CtzIOUtils.mkdirs;

/**
 * Utility methods for Spring resources API
 *
 * @author alexey
 * Date: 5/3/12
 */
public class CtzResourceUtils {
    // use only if you are sure about classloaders
    public static final PathMatchingResourcePatternResolver RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();
    public static final ResourceLoader RESOURCE_LOADER = RESOURCE_RESOLVER.getResourceLoader();

    public static File copyResourceToDir(String url, File dir) {
        mkdirs(dir);
        Resource re = RESOURCE_LOADER.getResource(url);
        File target = new File(dir, re.getFilename());
        return copyResource(re, target);
    }

    public static File copyResource(String url, File target) {
        Resource re = RESOURCE_LOADER.getResource(url);
        return copyResource(re, target);
    }

    public static File copyResource(Resource re, File target) {
        InputStream is = null;
        OutputStream os = null;
        try {
            if(!re.exists()) throw new IOException(format("Cannot load resource: '{}'", re));
            is = re.getInputStream();
            os = FileUtils.openOutputStream(target);
            IOUtils.copyLarge(is, os);
        } catch (IOException e) {
            throw new CtzIoException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
        return target;
    }

    // will work only with 'classpath:' and 'file:' resources
    public static boolean isDirectory(Resource resource) {
        try {
            if (ClassPathResource.class.isAssignableFrom(resource.getClass())) {
                return 0 == resource.getFilename().length();
            } else {
                return resource.getFile().isDirectory();
            }
        } catch (IOException e) {
            throw new CtzIoException(e);
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
            throw new CtzIoException(e);
        }
    }

    public static String readResourceToString(String path) {
        return readResourceToString(path, UTF8);
    }

    public static String readResourceToString(String path, String encoding) {
        InputStream is = null;
        try {
            is = RESOURCE_LOADER.getResource(path).getInputStream();
            return IOUtils.toString(is, encoding);
        } catch(IOException e) {
            throw new CtzIoException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static List<String> readResourceLines(String path) {
        return readResourceLines(path, UTF8);
    }

    public static List<String> readResourceLines(String path, String encoding) {
        InputStream is = null;
        try {
            is = RESOURCE_LOADER.getResource(path).getInputStream();
            return IOUtils.readLines(is, encoding);
        } catch(IOException e) {
            throw new CtzIoException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
