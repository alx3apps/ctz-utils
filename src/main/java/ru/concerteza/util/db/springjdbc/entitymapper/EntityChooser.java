package ru.concerteza.util.db.springjdbc.entitymapper;

import java.util.Map;
import java.util.Set;

/**
 * Interface for mapping rows for different entity classes
 *
 * @author alexey
 * Date: 5/16/12
 * @see EntityMapper
 */
public interface EntityChooser<T> {
    /**
     * @return list of all subclasses, that will be used in row mapping
     */
    Set<Class<? extends T>> subclasses();

    /**
     * @param dataMap row data before applying filters
     * @return class for concrete entity to instantiate for given row data
     */
    Class<? extends T> choose(Map<String, ?> dataMap);
}
