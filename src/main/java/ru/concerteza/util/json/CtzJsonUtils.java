package ru.concerteza.util.json;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.Resource;
import ru.concerteza.util.io.CtzResourceUtils;
import ru.concerteza.util.io.RuntimeIOException;
import ru.concerteza.util.string.CtzConstants;

import javax.annotation.Nullable;
import javax.crypto.MacSpi;
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
import static ru.concerteza.util.io.CtzResourceUtils.readResourceToString;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * JSON utilities using Gson
 *
 * @author alexey
 * Date: 10/17/11
 * @see CtzJsonUtilsTest
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

    /**
     * Wraps {@code JsonElement} into java map/iterable or extracts value from {@code JsonPrimitive}
     *
     * @param el json elementt
     * @return wrapped object or extracted value
     */
    @SuppressWarnings("unchecked")
    public static <T> T wrapJson(JsonElement el) {
        if(el.isJsonNull()) return null;
        if(el.isJsonPrimitive()) return (T) extractPrimitive(el.getAsJsonPrimitive());
        if(el.isJsonObject()) return (T) new JsonObjectAsMap(el.getAsJsonObject());
        if(el.isJsonArray()) return (T) Iterables.transform(el.getAsJsonArray(), WrapJsonFunction.INSTANCE);
        throw new IllegalStateException("Unknown type of input object: " + el);
    }

    private static Object extractPrimitive(JsonPrimitive obj) {
        if(obj.isBoolean()) return obj.getAsBoolean();
        if(obj.isNumber()) return obj.getAsNumber();
        if(obj.isString()) return obj.getAsString();
        throw new IllegalStateException(format("Unknown type of input object: '{}'", obj));
    }

    private enum WrapJsonFunction implements Function<JsonElement, Object> {
        INSTANCE;
        @Override
        public Object apply(JsonElement input) {
            return wrapJson(input);
        }
    }
}
