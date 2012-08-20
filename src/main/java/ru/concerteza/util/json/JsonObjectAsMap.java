package ru.concerteza.util.json;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.concerteza.util.json.CtzJsonUtils.wrapJson;

/**
 * Wrapper for {@code JsonObject} as {@link Map}
 *
 * @author alexey
 * Date: 8/10/12
 * @see CtzJsonUtilsTest
 */
public class JsonObjectAsMap implements Map<String, Object> {
    private final JsonObject delegate;

    /**
     * @param delegate json object to wrap
     */
    public JsonObjectAsMap(JsonObject delegate) {
        checkNotNull(delegate, "Provided JsonObject is null");
        this.delegate = delegate;
    }

    /**
     * Factory method
     *
     * @param json json element
     * @return map wrapper
     */
    public static JsonObjectAsMap of(JsonElement json) {
        checkArgument(json.isJsonObject(), "Input data: '%s' is not JsonObject", json);
        return new JsonObjectAsMap(json.getAsJsonObject());
    }

    /**
     * String input factory method
     *
     * @param json string
     * @return map wrapper
     */
    public static JsonObjectAsMap of(String json) {
        JsonElement el = new JsonParser().parse(json);
        checkArgument(el.isJsonObject(), "Input data: '%s' is not JsonObject", json);
        return new JsonObjectAsMap(el.getAsJsonObject());
    }

    /**
     * Reader input factory method, reads one {@code JsonElement} from provided reader
     *
     * @param json reader
     * @return map wrapper
     */
    public static JsonObjectAsMap of(Reader json) {
        JsonElement el = new JsonParser().parse(json);
        checkArgument(el.isJsonObject(), "Input data: '%s' is not JsonObject", el);
        return new JsonObjectAsMap(el.getAsJsonObject());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return delegate.has((String) key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        JsonElement el = delegate.get((String) key);
        if(null == el) return null;
        return wrapJson(el);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return delegate.entrySet().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return 0 == delegate.entrySet().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<Entry<String, Object>> entrySet() {
        return (Set) delegate.entrySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return delegate.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> keySet() {
        return ImmutableSet.copyOf(Collections2.transform(delegate.entrySet(), KeyFun.INSTANCE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Object> values() {
        return Collections2.transform(delegate.entrySet(), ValueFun.INSTANCE);
    }

    private enum KeyFun implements Function<Entry<String,JsonElement>, String> {
        INSTANCE;
        @Override
        public String apply(Entry<String, JsonElement> input) {
            return input.getKey();
        }
    }

    private enum ValueFun implements Function<Entry<String,JsonElement>, Object> {
        INSTANCE;
        @Override
        public JsonElement apply(Entry<String, JsonElement> input) {
            return input.getValue();
        }
    }

    // not implementation for other methods

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public JsonElement put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public JsonElement remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
         throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
