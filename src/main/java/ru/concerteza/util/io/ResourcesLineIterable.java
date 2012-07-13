package ru.concerteza.util.io;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * User: alexey
 * Date: 7/10/12
 */
public class ResourcesLineIterable implements Iterable<String> {
    private final List<String> paths;

    public ResourcesLineIterable(List<String> paths) {
        this.paths = paths;
    }

    @Override
    public Iterator<String> iterator() {
        List<Iterator<String>> iters = Lists.transform(paths, IterFun.INSTANCE);
        return Iterators.concat(iters.toArray(new Iterator[paths.size()]));
    }

    private enum IterFun implements Function<String, Iterator<String>>  {
        INSTANCE;
        @Override
        public AutocloseResourceLineIterator apply(@Nullable String input) {
            return new AutocloseResourceLineIterator(input);
        }
    }
}
