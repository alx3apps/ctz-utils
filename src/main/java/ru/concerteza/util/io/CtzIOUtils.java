package ru.concerteza.util.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.DefaultResourceLoader;
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

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: Oct 30, 2010
 */
public class CtzIOUtils {
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
        Resource[] resources = RESOURCE_RESOLVER.getResources(pattern);
        for(Resource re : resources) {
            doCopyResourcesToDir(re, dir);
        }
        return resources.length;
    }

    public static void copyResourceToDir(String url, File dir) throws IOException {
        mkdirs(dir);
        Resource re = RESOURCE_LOADER.getResource(url);
        doCopyResourcesToDir(re, dir);
    }

    private static void doCopyResourcesToDir(Resource re, File dir) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            if(!re.exists()) throw new IOException(format("Cannot load resource: '{}'", re));
            is = re.getInputStream();
            File target = new File(dir, re.getFilename());
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
        File tmp = File.createTempFile(clazz.getName(), ".tmp");
        boolean deleted = tmp.delete();
        if(!deleted) throw new IOException(format("Cannot delete temp file: '{}' while creating temp dir", tmp.getAbsolutePath()));
        boolean maked = tmp.mkdir();
        if(!maked) throw new IOException(format("Cannot create temp dir: '{}' while creating temp dir", tmp.getAbsolutePath()));
        tmp.deleteOnExit();
        return tmp;
    }
}
