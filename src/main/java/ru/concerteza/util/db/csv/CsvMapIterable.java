package ru.concerteza.util.db.csv;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.Resource;
import ru.concerteza.util.string.CtzConstants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static ru.concerteza.util.collection.CtzCollectionUtils.listsToMap;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;

/**
 * Map iterable implementation over CSV file
 *
 * @author alexey
 * Date: 6/29/12
 */
public class CsvMapIterable<T> implements Iterable<T> {
    private final Resource resource;
    private final Charset encoding;
    private final Splitter splitter;
    private final Function<Map<String, ?>, T> converter;

    /**
     * Shortcut constructor
     *
     * @param resourcePath spring resource path to CSV file
     * @param delimiter CSV fields delimiter
     */
    public CsvMapIterable(String resourcePath, String delimiter) {
        this(resourcePath, delimiter, CtzConstants.UTF8);
    }

    /**
     * Shortcut constructor
     *
     * @param resourcePath spring resource path to CSV file
     * @param delimiter CSV fields delimiter
     * @param encoding CSV file encoding
     */
    @SuppressWarnings("unchecked")
    public CsvMapIterable(String resourcePath, String delimiter, String encoding) {
        this(RESOURCE_LOADER.getResource(resourcePath), delimiter, encoding, (Function) Functions.identity());
    }

    /**
     * Shortcut constructor
     *
     * @param resourcePath spring resource path to CSV file
     * @param delimiter CSV fields delimiter
     * @param converter CSV row converter function
     */
    public CsvMapIterable(String resourcePath, String delimiter, Function<Map<String, ?>, T> converter) {
        this(RESOURCE_LOADER.getResource(resourcePath), delimiter, CtzConstants.UTF8, converter);
    }

    /**
     * Main constructor
     *
     * @param resource CSV file as spring resource
     * @param delimiter CSV fields delimiter
     * @param encoding CSV file encoding
     * @param converter CSV row converter function
     */
    public CsvMapIterable(Resource resource, String delimiter, String encoding, Function<Map<String, ?>, T> converter) {
        checkArgument(null != resource, "Resource is null");
        checkArgument(isNotEmpty(encoding), "Encoding is empty");
        checkArgument(isNotEmpty(encoding), "Splitter is empty");
        checkArgument(null != converter, "Converter is null");
        this.resource = resource;
        this.encoding = Charset.forName(encoding);
        this.splitter = Splitter.on(delimiter);
        this.converter = converter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return new CsvMapIterator();
    }

    private class CsvMapIterator implements Iterator<T> {
        private final LineIterator li;
        private final List<String> headers;

        private CsvMapIterator() {
            try {
                li = new LineIterator(new InputStreamReader(resource.getInputStream(), encoding));
                if(!li.hasNext()) throw new IOException("Cannot read CSV headers, input resource is empty");
                headers = parseList(li.next());
            } catch(IOException e) {
                throw new UnhandledException(e);
            }
        }

        @Override
        public boolean hasNext() {
            boolean res = li.hasNext();
            if(!res) li.close();
            return res;
        }

        @Override
        public T next() {
            Map<String, String> data = parseMap(li.next());
            return converter.apply(data);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private Map<String, String> parseMap(String line) {
            List<String> values = parseList(line);
            checkArgument(headers.size() == values.size(), "Invalid CSV line, headers count: '%s', values count: '%s', line: '%s'", headers, values, line);
            return listsToMap(headers, values);
        }

        private List<String> parseList(String line) {
            checkArgument(isNotBlank(line), "CSV input line is blank, resource: '%s', line: '%s'", resource, line);
            return ImmutableList.copyOf(splitter.split(line));
        }
    }
}
