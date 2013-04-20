package ru.concerteza.util.db;

import com.alexkasko.springjdbc.typedqueries.common.PlainSqlQueriesParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;
import static ru.concerteza.util.string.CtzConstants.UTF8;

/**
 * Helper method for loading SQL queries maps
 *
 * @author alexkasko
 * Date: 4/20/13
 */
public class CtzTypedQueriesUtils {
    public static Map<String, String> loadSql(String resourcePath) {
        InputStream is = null;
        try {
            is = RESOURCE_LOADER.getResource(resourcePath).getInputStream();
            return new PlainSqlQueriesParser().parse(is, UTF8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(is);
        }
    }
}
