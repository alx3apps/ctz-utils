package ru.concerteza.util.json;

import com.google.common.base.Optional;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 *  JSON type for Guava optional, maps absent to null and present to standard JSON
 *  for that class
 *
 * @author alexey
 * Date: 8/14/12
 */
public class OptionalTypeAdapter implements JsonSerializer<Optional<?>>, JsonDeserializer<Optional<?>> {
    @Override
    public JsonElement serialize(Optional<?> src, Type typeOfSrc, JsonSerializationContext context) {
        if(!src.isPresent()) return JsonNull.INSTANCE;
        return context.serialize(src.get());
    }

    @Override
    public Optional<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Optional.fromNullable(context.deserialize(json, typeOfT));
    }
}
