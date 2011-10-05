package ru.concerteza.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: alexey
 * Date: Oct 30, 2010
 */
public class CtzIOUtils {
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

    public static File getJarParentDir(Class<?> clazz) {
        try {
            URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
            return new File(uri).getParentFile();
        } catch (Exception e) {
            throw new UnhandledException(e);
        }
    }

    public static void appendToFile(final InputStream in, final File f) throws IOException {
        OutputStream stream = null;
        try {
            stream = outStream(f);
            IOUtils.copy(in, stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static void appendToFile(final String in, final File f) throws IOException {
        InputStream stream = null;
        try {
            stream = IOUtils.toInputStream(in, CtzConstants.UTF8);
            appendToFile(stream, f);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private static OutputStream outStream(final File f) throws IOException {
        return new BufferedOutputStream(new FileOutputStream(f, true));
    }
}
