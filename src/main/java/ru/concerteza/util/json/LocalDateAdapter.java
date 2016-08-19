package ru.concerteza.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * GSON adapter for joda-time's LocalDate class. Serialized into {@code yyyy-MM-dd} format
 * that is also supported by {@link java.sql.Date#valueOf(String)}
 *
 * @author alexey
 * Date: 7/2/12
 * @see LocalDateTimeAdapter
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return LocalDate.parse(json.getAsString(), DTF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DTF.format(src));
    }
}
