package ru.concerteza.util.json;

import com.google.gson.*;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ru.concerteza.util.date.CtzDateUtils;

import java.lang.reflect.Type;

import static ru.concerteza.util.date.CtzDateUtils.DEFAULT_LDT_FORMAT;

/**
 * GSON adapter for joda-time's LocalDateTime class. Serialized into {@code yyyy-MM-dd HH:mm:ss} format
 * that is also supported by {@link java.sql.Timestamp#valueOf(String)}
 *
 * @author alexey
 * Date: 7/2/12
 * @see LocalDateTime
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return DEFAULT_LDT_FORMAT.parseLocalDateTime(json.getAsString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DEFAULT_LDT_FORMAT.print(src));
    }
}
