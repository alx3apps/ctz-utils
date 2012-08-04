package ru.concerteza.util.db.springjdbc.named;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * User: alexey
 * Date: 7/5/12
 */

// todo!!!!!!!
class NamedConstructor<T> {
    private final Constructor<T> constructor;
    private final List<String> names;

    @SuppressWarnings("unchecked")
    public NamedConstructor(Constructor<?> constructor, List<String> names) {
        this.constructor = (Constructor<T>) constructor;
        if(!this.constructor.isAccessible()) constructor.setAccessible(true);
        this.names = names;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public List<String> getNames() {
        return names;
    }
}
