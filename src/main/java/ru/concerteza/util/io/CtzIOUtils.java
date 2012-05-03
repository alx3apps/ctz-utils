package ru.concerteza.util.io;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: Oct 30, 2010
 */
public class CtzIOUtils {
    // use only if you sure about classloaders
    public static final PathMatchingResourcePatternResolver RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();
    public static final ResourceLoader RESOURCE_LOADER = RESOURCE_RESOLVER.getResourceLoader();

    private static final ResourceDirectoryPredicate RESOURCE_DIRECTORY_PREDICATE = new ResourceDirectoryPredicate();

    public static void closeQuietly(XMLEventReader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (XMLStreamException ioe) {
            // ignore
        }
    }

    public static void closeQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    public static void closeQuietly(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    public static File codeSourceDir(Class<?> clazz) {
        try {
            URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
            File jarOrDir = new File(uri);
            return jarOrDir.isDirectory() ? jarOrDir : jarOrDir.getParentFile();
        } catch (Exception e) {
            throw new UnhandledException(e);
        }
    }

    public static void appendToFile(File file, String in, String encoding) throws RuntimeIOException {
        InputStream stream = null;
        try {
            stream = IOUtils.toInputStream(in, encoding);
            appendToFile(file, stream);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static void appendToFile(final File f, final InputStream in) throws RuntimeIOException {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(f, true);
            IOUtils.copy(in, stream);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static int copyResourceListToDir(String pattern, File dir) throws RuntimeIOException {
        mkdirs(dir);
        CopyResourceToDirFunction fun = new CopyResourceToDirFunction(dir);
        return processResourceList(pattern, RESOURCE_DIRECTORY_PREDICATE, fun).size();
    }

    public static void copyResourceToDir(String url, File dir) throws RuntimeIOException {
        mkdirs(dir);
        Resource re = RESOURCE_LOADER.getResource(url);
        doCopyResourceToDir(re, dir);
    }

    public static void copyResource(String url, File target) throws RuntimeIOException {
        Resource re = RESOURCE_LOADER.getResource(url);
        doCopyResource(re, target);
    }

    public static <T> List<T> processResourceList(String pattern, Predicate<Resource> filter, Function<Resource, T> fun) throws RuntimeIOException {
        try {
            List<Resource> resources = asList(RESOURCE_RESOLVER.getResources(pattern));
            Iterable<Resource> filtered = Iterables.filter(resources, filter);
            Iterable<T> applied = Iterables.transform(filtered, fun);
            return ImmutableList.copyOf(applied);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private static void doCopyResourceToDir(Resource re, File dir) throws RuntimeIOException {
        File target = new File(dir, re.getFilename());
        doCopyResource(re, target);
    }

    private static void doCopyResource(Resource re, File target) throws RuntimeIOException {
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

    public static void mkdirs(File dir) throws RuntimeIOException {
        if(dir.exists()) {
            if(dir.isFile()) throw new RuntimeIOException(format("Cannot write to directory: '{}'", dir.getAbsolutePath()));
        } else {
            boolean res = dir.mkdirs();
            if(!res) throw new RuntimeIOException(format("Cannot create directory: '{}'", dir.getAbsolutePath()));
        }
    }

    public static File createTmpFile(Class<?> clazz) throws RuntimeIOException {
        try {
            File tmp = File.createTempFile(clazz.getName(), ".tmp");
            tmp.deleteOnExit();
            return tmp;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static File createTmpDir(Class<?> clazz) throws RuntimeIOException {
        try {
            File baseDir = new File(System.getProperty("java.io.tmpdir"));
            String baseName = format("{}_{}.tmp", clazz.getName(), currentTimeMillis());
            File tmp = new File(baseDir, baseName);
            boolean res = tmp.mkdirs();
            if (!res) throw new IOException(format("Cannot create directory: '{}'", tmp.getAbsolutePath()));
            tmp.deleteOnExit();
            return tmp;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private static class ResourceDirectoryPredicate implements Predicate<Resource> {
        @Override
        public boolean apply(Resource input) {
            return input.getFilename().length() > 0;
        }
    }

    private static class CopyResourceToDirFunction implements Function<Resource, Boolean> {
        private final File dir;

        private CopyResourceToDirFunction(File dir) {
            this.dir = dir;
        }

        @Override
        public Boolean apply(Resource input) {
            doCopyResourceToDir(input, dir);
            // to count copied files in caller,
            // cannot return Void null here for further chaining
            return true;
        }
    }
}
