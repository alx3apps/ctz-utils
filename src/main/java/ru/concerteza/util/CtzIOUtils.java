package ru.concerteza.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.io.File;
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
}
