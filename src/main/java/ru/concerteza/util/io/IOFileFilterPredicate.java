package ru.concerteza.util.io;


import com.google.common.base.Predicate;
import org.apache.commons.io.filefilter.IOFileFilter;

import javax.annotation.Nullable;
import java.io.File;

/**
 * User: alexey
 * Date: 5/8/12
 */
public class IOFileFilterPredicate implements Predicate<File> {
    private final IOFileFilter filter;

    private IOFileFilterPredicate(IOFileFilter filter) {
        this.filter = filter;
    }

    public static IOFileFilterPredicate of(IOFileFilter filter) {
        return new IOFileFilterPredicate(filter);
    }

    @Override
    public boolean apply(File input) {
        return filter.accept(input);
    }
}
