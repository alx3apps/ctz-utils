package ru.concerteza.util.io.holder;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.LineIterator;
import org.springframework.core.io.Resource;
import ru.concerteza.util.collection.SingleUseIterable;
import ru.concerteza.util.io.CtzResourceUtils;
import ru.concerteza.util.namedregex.NamedMatcher;
import ru.concerteza.util.namedregex.NamedPattern;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import static org.apache.commons.io.IOUtils.lineIterator;

/**
 * Abstract class for parsing JSON list files. List access/transformations should be done by inheritors.
 *
 * @author alexey
 * Date: 6/25/12
 */
public abstract class ListJsonHolder extends JsonHolder {
    protected List<String> list;

    /**
     * Should be called after inheritors fields injection or manually without spring
     */
    @PostConstruct
    protected void postConstruct() throws IOException {
        NamedPattern valuePattern = NamedPattern.compile(valueRegex());
        NamedPattern commentPattern = NamedPattern.compile(commentRegex());
        LineIterator lr = null;
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        try {
            Resource res = CtzResourceUtils.RESOURCE_LOADER.getResource(jsonFilePath());
            lr = lineIterator(res.getInputStream(), encoding());
            Iterable<String> iter = SingleUseIterable.of(lr);
            for(String line : iter) {
                if(commentPattern.matcher(line).matches()) continue;
                NamedMatcher matcher = valuePattern.matcher(line);
                if(matcher.matches()) builder.add(matcher.group("value"));
            }
            this.list = builder.build();
        } finally {
            LineIterator.closeQuietly(lr);
        }
    }

    /**
     * @return named regex to extract 'value', e.g. <code>^\s*(?<value>.+)\s*$</code>
     */
    protected abstract String valueRegex();

    /**
     * @return regex to check whether string is comment, e.g. <code>^\s*#.*$</code>
     */
    protected abstract String commentRegex();

}
