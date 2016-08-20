package ru.concerteza.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GSON adapter for {@link LocalDateTime}. Serialized into {@code yyyy-MM-dd HH:mm:ss} format
 * that is also supported by {@link java.sql.Timestamp#valueOf(String)}
 *
 * @author alexey
 * Date: 7/2/12
 * @see LocalDateTime
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return LocalDateTime.parse(json.getAsString(), DTF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DTF.format(src));
    }
}
