package ru.concerteza.util.io;

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

import static java.lang.System.currentTimeMillis;
import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: Oct 30, 2010
 */
public class CtzIOUtils {
    // use only if you sure about classloaders
    public static final PathMatchingResourcePatternResolver RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();
    public static final ResourceLoader RESOURCE_LOADER = RESOURCE_RESOLVER.getResourceLoader();

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

    public static void appendToFile(final File file, final String in, String encoding) throws IOException {
        InputStream stream = null;
        try {
            stream = IOUtils.toInputStream(in, encoding);
            appendToFile(file, stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static void appendToFile(final File f, final InputStream in) throws IOException {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(f, true);
            IOUtils.copy(in, stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static int copyResourceListToDir(String pattern, File dir) throws IOException {
        mkdirs(dir);
        int count = 0;
        for(Resource re : RESOURCE_RESOLVER.getResources(pattern)) {
            if(re.getFilename().length() > 0) { // filter directories
                doCopyResourceToDir(re, dir);
                count += 1;
            }
        }
        return count;
    }

    public static void copyResourceToDir(String url, File dir) throws IOException {
        mkdirs(dir);
        Resource re = RESOURCE_LOADER.getResource(url);
        doCopyResourceToDir(re, dir);
    }

    public static void copyResource(String url, File target) throws IOException {
        Resource re = RESOURCE_LOADER.getResource(url);
        doCopyResource(re, target);
    }

    private static void doCopyResourceToDir(Resource re, File dir) throws IOException {
        File target = new File(dir, re.getFilename());
        doCopyResource(re, target);
    }

    private static void doCopyResource(Resource re, File target) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            if(!re.exists()) throw new IOException(format("Cannot load resource: '{}'", re));
            is = re.getInputStream();
            os = FileUtils.openOutputStream(target);
            IOUtils.copyLarge(is, os);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    private static void mkdirs(File dir) throws IOException {
        if(dir.exists()) {
            if(dir.isFile()) throw new IOException(format("Cannot write to directory: '{}'", dir.getAbsolutePath()));
        } else {
            boolean res = dir.mkdirs();
            if(!res) throw new IOException(format("Cannot create directory: '{}'", dir.getAbsolutePath()));
        }
    }

    public static File createTmpFile(Class<?> clazz) throws IOException {
        File tmp = File.createTempFile(clazz.getName(), ".tmp");
        tmp.deleteOnExit();
        return tmp;
    }

    public static File createTmpDir(Class<?> clazz) throws IOException {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = format("{}_{}.tmp", clazz.getName(), currentTimeMillis());
        File tmp = new File(baseDir, baseName);
        boolean res = tmp.mkdirs();
        if(!res) throw new IOException(format("Cannot create directory: '{}'", tmp.getAbsolutePath()));
        tmp.deleteOnExit();
        return tmp;
    }
}
