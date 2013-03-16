package ru.concerteza.util.io;

import com.google.common.collect.AbstractIterator;
import org.apache.commons.io.LineIterator;
import org.springframework.core.io.Resource;
import ru.concerteza.util.string.CtzConstants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;

/**
 * User: alexey
 * Date: 7/10/12
 */
@Deprecated // use FinishableIterator
public class AutocloseResourceLineIterator extends AbstractIterator<String> implements Iterator<String> {
    private final LineIterator li;

    // todo encoding
    public AutocloseResourceLineIterator(String path) {
        Resource re = RESOURCE_LOADER.getResource(path);
        try {
            this.li = new LineIterator(new InputStreamReader(re.getInputStream(), CtzConstants.UTF8));
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    protected String computeNext() {
        if(li.hasNext()) return li.next();
        LineIterator.closeQuietly(li);
        return endOfData();
    }
}
