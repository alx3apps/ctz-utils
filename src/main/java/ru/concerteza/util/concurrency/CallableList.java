package ru.concerteza.util.concurrency;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang.UnhandledException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * User: alexey
 * Date: 5/5/12
 */
public class CallableList<T> implements Callable<List<T>> {
    private final List<Callable<? extends T>> list = new ArrayList<Callable<? extends T>>();
    private final Fun fun = new Fun();

    public CallableList<T> add(Callable<? extends T> callable) {
        list.add(callable);
        return this;
    }

    @Override
    public List<T> call() throws Exception {
        List<? extends T> results = Lists.transform(list, fun);
        return ImmutableList.copyOf(results);
    }

    private class Fun implements Function<Callable<? extends T>, T> {
        @Override
        public T apply(Callable<? extends T> input) {
            try {
                return input.call();
            } catch (Exception e) {
                throw new UnhandledException(e);
            }
        }
    }
}