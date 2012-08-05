package ru.concerteza.util.json;

import com.google.gson.*;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

/**
 * GSON adapter for joda-time's LocalDate class. Serialized into {@code yyyy-MM-dd} format
 * that is also supported by {@link java.sql.Date#valueOf(String)}
 *
 * @author alexey
 * Date: 7/2/12
 * @see LocalDateTimeAdapter
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd");

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return DTF.parseLocalDate(json.getAsString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DTF.print(src));
    }
}
