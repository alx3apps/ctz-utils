package ru.concerteza.util.json;

import com.google.common.base.Optional;
import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *  JSON type adapter for Guava optional, maps absent to null and present to standard JSON
 *  for optional value.
 *  Must be registered ad follows:
 *  <code>
 *      new GsonBuilder()
 *      .registerTypeAdapter(Optional.class, new OptionalTypeAdapter())
 *      .registerTypeAdapter(Optional.absent().getClass(), new OptionalTypeAdapter())
 *      .registerTypeAdapter(Optional.of(42).getClass(), new OptionalTypeAdapter())
 *  </code>
 *
 * @author alexey
 * Date: 8/14/12
 */
@Deprecated // use OptionalTypeAdapterFactory
public class OptionalTypeAdapter implements JsonSerializer<Optional<?>>, JsonDeserializer<Optional<?>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(Optional src, Type typeOfSrc, JsonSerializationContext context) {
        if(!src.isPresent()) return JsonNull.INSTANCE;
        return context.serialize(src.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonNull()) return Optional.absent();
        ParameterizedType pt = (ParameterizedType) typeOfT;
        Type[] genericArr = pt.getActualTypeArguments();
        checkArgument(1 == genericArr.length, "ActualTypeArguments array must have size 1, but was: '%s'", genericArr);
        return Optional.of(context.deserialize(json, genericArr[0]));
    }
}
