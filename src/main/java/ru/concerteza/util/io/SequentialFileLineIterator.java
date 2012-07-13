package ru.concerteza.util.io;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import org.apache.commons.io.filefilter.IOFileFilter;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Iterator;

import static ru.concerteza.util.io.CtzIOUtils.iterateFiles;

/**
 * User: alexey
 * Date: 7/12/12
 */
public class SequentialFileLineIterator implements Iterator<String> {
    private final Iterator<String> lines;

    public SequentialFileLineIterator(File root, IOFileFilter fileFilter, IOFileFilter dirFilter, String encoding) {
        Iterator<File> files = iterateFiles(root, fileFilter, dirFilter, false).iterator();
        Iterator<CloseFileOnFinishLineIterator> iters = Iterators.transform(files, new LineIterFun(encoding));
        this.lines = Iterators.concat(iters);
    }

    @Override
    public boolean hasNext() {
        return lines.hasNext();
    }

    @Override
    public String next() {
        return lines.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    private static class LineIterFun implements Function<File, CloseFileOnFinishLineIterator> {
        private final String encoding;

        private LineIterFun(String encoding) {
            this.encoding = encoding;
        }

        @Override
        public CloseFileOnFinishLineIterator apply(@Nullable File input) {
            return new CloseFileOnFinishLineIterator(input, encoding);
        }
    }
}
