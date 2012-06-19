package ru.concerteza.util.concurrency;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang.UnhandledException;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Callable, wraps list of callable and call them one by one. Callables may be added to list.
 * Thread-safe.
 *
 * @author alexey
 * Date: 5/5/12
 * @see CallableListTest
 */
public class CallableList<T> implements Callable<List<T>> {
    private final List<Callable<? extends T>> list = Lists.newArrayList();
    private final Object listLock = new Object();
    private final Fun fun = new Fun();

    /**
     * Adds callable to inner callables list
     *
     * @param callable callable to add to list
     * @return itself
     */
    public CallableList<T> add(Callable<? extends T> callable) {
        synchronized(listLock) {
            list.add(callable);
            return this;
        }
    }

    /**
     * @return list of inner callable results
     * @throws Exception
     */
    @Override
    public List<T> call() throws Exception {
        ImmutableList<Callable<? extends T>> il;
        synchronized(listLock) {
            il = ImmutableList.copyOf(list);
        }
        List<? extends T> results = Lists.transform(il, fun);
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