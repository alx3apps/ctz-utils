package ru.concerteza.util.json;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.Resource;
import ru.concerteza.util.io.CtzResourceUtils;
import ru.concerteza.util.io.RuntimeIOException;
import ru.concerteza.util.string.CtzConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * JSON utilities
 *
 * @author alexey
 * Date: 10/17/11
 */
public class CtzJsonUtils {
    /**
     * GSON Type for {@code HashMap<String, String>>} type
     */
    public static final Type STRING_MAP_TYPE = new TypeToken<HashMap<String, String>>() {}.getType();
    /**
     * GSON Type for {@code List<String>>} type
     */
    public static final Type STRING_LIST_TYPE = new TypeToken<List<String>>() {}.getType();

    /**
     * Parses spring resource into {@code HashMap<String, String>>} type
     *
     * @param resourcePath spring resource path
     * @return map parsed from JSON
     */
    public static Map<String, String> parseStringMap(String resourcePath) {
        return parseMap(RESOURCE_LOADER.getResource(resourcePath), CtzConstants.UTF8);
    }

    /**
     * Parses spring resource into {@code HashMap<String, String>>} type
     *
     * @param resource spring resource
     * @return map parsed from JSON
     */
    public static Map<String, String> parseStringMap(Resource resource) {
        return parseMap(resource, CtzConstants.UTF8);
    }

    /**
     * Parses spring resource into {@code HashMap<String, String>>} type
     *
     * @param resource spring resource
     * @param encoding resource encoding
     * @return map parsed from JSON
     */
    public static Map<String, String> parseMap(Resource resource, String encoding) {
        Map<String, String> map = parseResource(resource, STRING_MAP_TYPE, encoding);
        return ImmutableMap.copyOf(map);
    }

    /**
     * Parses spring resource into provided object type
     *
     * @param resource spring resource
     * @param type result type
     * @param encoding resource encoding
     * @param <T> result type parameter
     * @return object parsed from JSON
     * @throws RuntimeIOException on IO error
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseResource(Resource resource, Type type, String encoding) {
        InputStream jsonStream = null;
        try {
            if(!resource.exists()) throw new RuntimeIOException(format("Cannot load resource: '{}'", resource));
            jsonStream = resource.getInputStream();
            InputStreamReader reader = new InputStreamReader(jsonStream, Charset.forName(encoding));
            return (T) new Gson().fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            closeQuietly(jsonStream);
        }
    }
}
