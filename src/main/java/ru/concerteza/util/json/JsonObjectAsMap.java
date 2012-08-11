package ru.concerteza.util.json;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang.UnhandledException;

import javax.annotation.Nullable;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: alexey
 * Date: 8/10/12
 */
public class JsonObjectAsMap implements Map<String, Object> {
    private final WrapFun wrapFun = new WrapFun();
    private final JsonObject delegate;

    public JsonObjectAsMap(JsonObject delegate) {
        checkNotNull(delegate, "Provided JsonObject is null");
        this.delegate = delegate;
    }

    public static JsonObjectAsMap of(String json) {
        JsonElement el = new JsonParser().parse(json);
        return new JsonObjectAsMap(el.getAsJsonObject());
    }

    public static JsonObjectAsMap of(Reader json) {
        JsonElement el = new JsonParser().parse(json);
        return new JsonObjectAsMap(el.getAsJsonObject());
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.has((String) key);
    }

    @Override
    public Object get(Object key) {
        JsonElement el = delegate.get((String) key);
        return wrap(el);
    }

    private Object wrap(JsonElement el) {
        if(el.isJsonNull()) return null;
        if(el.isJsonPrimitive()) return extractValue(el.getAsJsonPrimitive());
        if(el.isJsonObject()) return new JsonObjectAsMap(el.getAsJsonObject());
        if(el.isJsonArray()) return Iterables.transform(el.getAsJsonArray(), wrapFun);
        throw new IllegalStateException("Unknown type of input object: " + el);
    }

    // todo removeme
    private Object extractValue(JsonPrimitive obj) {
        if(obj.isBoolean()) return obj.getAsBoolean();
        if(obj.isNumber()) return obj.getAsNumber();
        if(obj.isString()) return obj.getAsString();
        throw new IllegalStateException("Unknown type of input object: " + obj);
    }

    private class WrapFun implements Function<JsonElement, Object> {
        @Override
        public Object apply(@Nullable JsonElement input) {
            return wrap(input);
        }
    }

    // not implementation for other methods

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
         throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Object> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
