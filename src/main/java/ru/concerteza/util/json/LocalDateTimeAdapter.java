package ru.concerteza.util.json;

import com.google.gson.*;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

/**
 * GSON adapter for joda-time's LocalDateTime class. Serialized into {@code yyyy-MM-dd HH:mm:ss} format
 * that is also supported by {@link java.sql.Timestamp#valueOf(String)}
 *
 * @author alexey
 * Date: 7/2/12
 * @see LocalDateTime
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return DTF.parseLocalDateTime(json.getAsString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DTF.print(src));
    }
}
