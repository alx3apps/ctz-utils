package ru.concerteza.util.json;

import com.google.common.collect.AbstractIterator;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;
import java.io.Reader;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Wraps {@link Reader} with JSON array as {@link java.util.Iterator}
 *
 * @author alexkasko
 * Date: 7/12/13
 */
public class JsonParseIterator<T> extends AbstractIterator<T> {
    private final Class<T> clazz;
    private final JsonReader reader;
    private final Gson gson;

    /**
     * Constructor
     *
     * @param gson gson instance
     * @param reader input reader
     * @param clazz target class
     */
    public JsonParseIterator(Gson gson, Reader reader, Class<T> clazz) {
        checkNotNull(gson, "Provided gson is null");
        checkNotNull(reader, "Provided reader is null");
        checkNotNull(clazz, "Provided class is null");
        try {
            this.clazz = clazz;
            this.gson = gson;
            this.reader = new JsonReader(reader);
            this.reader.setLenient(true);
            this.reader.beginArray();
        } catch (IOException e) {
            throw new CtzJsonException(e);
        }
    }

    /**
     * Generic-friendly factory method
     *
     * @param gson gson instance
     * @param reader input reader
     * @param clazz target class
     * @param <T> class generic parameter
     * @return iterator instance
     */
    public static <T> JsonParseIterator<T> jsonParseIterator(Gson gson, Reader reader, Class<T> clazz) {
        return new JsonParseIterator<T>(gson, reader, clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected T computeNext() {
        try {
            if (reader.hasNext()) {
                return (T) gson.fromJson(reader, clazz);
            } else {
                reader.endArray();
                reader.close();
                return endOfData();
            }
        } catch (IOException e) {
            throw new CtzJsonException(e);
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).
                append("clazz", clazz).
                append("reader", reader).
                append("gson", gson).
                toString();
    }
}
