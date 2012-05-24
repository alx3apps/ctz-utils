package ru.concerteza.util.db.springjdbc.entitymapper;

import java.util.Map;
import java.util.Set;

/**
 * Interface for mapping rows for different entity classes
 *
 * @author alexey
 * Date: 5/16/12
 * @see EntityMapper
 * @see SubclassesEntityMapper
 */
public interface SubclassChooser<T> {
    /**
     * @return list of all subclasses, that will be used in row mapping
     */
    Set<Class<? extends T>> subclasses();

    /**
     * @param dataMap row data after applying all filters
     * @return class for concrete entity to instantiate for given row data
     */
    Class<? extends T> choose(Map<String, Object> dataMap);
}
