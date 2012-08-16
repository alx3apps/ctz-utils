package ru.concerteza.util.json;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * JSON type adapter factory for Guava optional, maps absent to null and present to standard JSON
 * for optional value.
 *
 * @author alexey
 * Date: 8/16/12
 */
public class OptionalTypeAdapterFactory implements TypeAdapterFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(!Optional.class.isAssignableFrom(type.getRawType())) return null;
        return (TypeAdapter) new OptionalTypeAdapter(gson, type);
    }

    private static class OptionalTypeAdapter extends TypeAdapter<Optional> {
        private final Gson currentGson;
        private final TypeToken tt;

        private OptionalTypeAdapter(Gson currentGson, TypeToken tt) {
            this.currentGson = currentGson;
            this.tt = tt;
        }

        @Override
        public void write(JsonWriter out, Optional value) throws IOException {
            if(!value.isPresent()) out.nullValue();
            else currentGson.toJson(value.get(), value.get().getClass(), out);
        }

        @Override
        public Optional read(JsonReader in) throws IOException {
            if(JsonToken.NULL.equals(in.peek())) return Optional.absent();
            ParameterizedType pt = (ParameterizedType) tt.getType();
            Type valueType = pt.getActualTypeArguments()[0];
            Object res = currentGson.fromJson(in, valueType);
            return Optional.of(res);
        }
    }
}
