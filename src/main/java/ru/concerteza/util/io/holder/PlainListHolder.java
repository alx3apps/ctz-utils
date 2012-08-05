package ru.concerteza.util.io.holder;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.LineIterator;
import org.springframework.core.io.Resource;
import ru.concerteza.util.collection.SingleUseIterable;
import ru.concerteza.util.io.CtzResourceUtils;
import ru.concerteza.util.io.RuntimeIOException;
import ru.concerteza.util.namedregex.NamedMatcher;
import ru.concerteza.util.namedregex.NamedPattern;
import ru.concerteza.util.string.CtzConstants;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import static org.apache.commons.io.IOUtils.lineIterator;

/**
 * Abstract class for parsing list files. List access/transformations should be done by inheritors.
 *
 * @author alexey
 * Date: 6/25/12
 * @see PlainListHolderTest
 */
public abstract class PlainListHolder {
    protected List<String> list;

    /**
     * Should be called after inheritors fields injection or manually without spring
     */
    @PostConstruct
    protected void postConstruct() {
        NamedPattern valuePattern = NamedPattern.compile(valueRegex());
        NamedPattern commentPattern = NamedPattern.compile(commentRegex());
        LineIterator lr = null;
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        try {
            Resource res = CtzResourceUtils.RESOURCE_LOADER.getResource(filePath());
            lr = lineIterator(res.getInputStream(), encoding());
            Iterable<String> iter = SingleUseIterable.of(lr);
            for(String line : iter) {
                if(commentPattern.matcher(line).matches()) continue;
                NamedMatcher matcher = valuePattern.matcher(line);
                if(matcher.matches()) builder.add(matcher.group("value"));
            }
            this.list = builder.build();
        } catch(IOException e) {
            throw new RuntimeIOException(e);
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

    /**
     * @return spring resource path to file
     */
    protected abstract String filePath();

    /**
     * @return resource encoding, UTF-8 by default
     */
    protected String encoding() {
        return CtzConstants.UTF8;
    }

}
