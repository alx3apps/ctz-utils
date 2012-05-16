package ru.concerteza.util.db.springjdbc.entitymapper;

import java.util.Map;
import java.util.Set;

/**
 * User: alexey
 * Date: 5/16/12
 */
public interface SubclassChooser<T> {
    Set<Class<? extends T>> subclasses();

    Class<? extends T> choose(Map<String, Object> dataMap);
}
