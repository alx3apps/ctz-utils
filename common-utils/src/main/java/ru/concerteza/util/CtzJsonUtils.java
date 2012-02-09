package ru.concerteza.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 10/17/11
 */
public class CtzJsonUtils {
    public static final Type STRING_MAP_TYPE = new TypeToken<HashMap<String, String>>() {}.getType();
    public static final Type STRING_LIST_TYPE = new TypeToken<List<String>>() {}.getType();

    public static Map<String, String> parseStringMap(Resource resource) {
        return parseMap(resource, CtzConstants.UTF8);
    }

    public static Map<String, String> parseMap(Resource resource, String encoding) {
        Map<String, String> map = parseResource(resource, STRING_MAP_TYPE, encoding);
        return ImmutableMap.copyOf(map);
    }

    @SuppressWarnings("unchecked")
    public static <T> T parseResource(Resource resource, Type type, String encoding) {
        InputStream jsonStream = null;
        try {
            jsonStream = resource.getInputStream();
            checkNotNull(jsonStream, "Cannot load resource: %s", resource);
            InputStreamReader reader = new InputStreamReader(jsonStream, Charset.forName(encoding));
            return (T) new Gson().fromJson(reader, type);
        } catch (IOException e) {
            throw new UnhandledException(e);
        } finally {
            IOUtils.closeQuietly(jsonStream);
        }
    }
}
