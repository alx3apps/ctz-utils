package ru.concerteza.util.db.csv;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.UnhandledException;
import org.springframework.core.io.Resource;
import ru.concerteza.util.CtzConstants;

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
 * User: alexey
 * Date: 6/29/12
 */
public class CsvMapIterable implements Iterable<Map<String, ?>> {
    private final String resourcePath;
    private final Charset encoding;
    private final Splitter splitter;
    private final List<Function<Map<String,?>, Map<String,?>>> converters;

    public CsvMapIterable(String resourcePath, String delimiter, Function<Map<String,?>, Map<String, ?>>... converters) {
        this(resourcePath, delimiter, CtzConstants.UTF8, converters);
    }

    public CsvMapIterable(String resourcePath, String delimiter, String encoding, Function<Map<String,?>, Map<String, ?>>... filters) {
        checkArgument(isNotEmpty(resourcePath), "Resource path is empty");
        checkArgument(isNotEmpty(encoding), "Encoding is empty");
        checkArgument(isNotEmpty(encoding), "Splitter is empty");
        this.resourcePath = resourcePath;
        this.encoding = Charset.forName(encoding);
        this.splitter = Splitter.on(delimiter);
        this.converters = ImmutableList.copyOf(filters);
    }

    @Override
    public Iterator<Map<String, ?>> iterator() {
        return new CsvMapIterator();
    }

    private class CsvMapIterator implements Iterator<Map<String, ?>> {
        private final LineIterator li;
        private final List<String> headers;

        private CsvMapIterator() {
            Resource re = RESOURCE_LOADER.getResource(resourcePath);
            try {
                li = new LineIterator(new InputStreamReader(re.getInputStream(), encoding));
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
        public Map<String, ?> next() {
            Map<String, ?> data = parseMap(li.next());
            for(Function<Map<String,?>, Map<String, ?>> fi : converters) {
                data = fi.apply(data);
            }
            return data;
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
            checkArgument(isNotBlank(line), "CSV input line is blank, resource: '%s', line: '%s'", resourcePath, line);
            return ImmutableList.copyOf(splitter.split(line));
        }
    }
}
