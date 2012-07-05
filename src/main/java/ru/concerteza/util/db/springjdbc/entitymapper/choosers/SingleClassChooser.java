package ru.concerteza.util.db.springjdbc.entitymapper.choosers;

import com.google.common.collect.ImmutableSet;
import ru.concerteza.util.db.springjdbc.entitymapper.EntityChooser;

import java.util.Map;
import java.util.Set;

/**
 * User: alexey
 * Date: 6/24/12
 */
public class SingleClassChooser<T> implements EntityChooser<T> {
    private final Class<T> clazz;

    protected SingleClassChooser(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <T> SingleClassChooser<T> forClass(Class<T> clazz) {
        return new SingleClassChooser<T>(clazz);
    }

    @Override
    public Set<Class<? extends T>> subclasses() {
        return (Set) ImmutableSet.of(clazz);
    }

    @Override
    public Class<? extends T> choose(Map<String, ?> dataMap) {
        return clazz;
    }
}
