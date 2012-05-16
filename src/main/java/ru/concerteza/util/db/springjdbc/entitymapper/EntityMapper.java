package ru.concerteza.util.db.springjdbc.entitymapper;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * User: alexey
 * Date: 4/14/12
 */

// note: will hold reference to class (and its subclasses in case of ChooserFilter)
// http://frankkieviet.blogspot.com/2006/10/classloader-leaks-dreaded-permgen-space.html
public abstract class EntityMapper<T> implements RowMapper<T> {
    protected final ColumnMapRowMapper mapper = new ColumnMapRowMapper();
    protected final List<Filter> filters;

    protected EntityMapper(Filter... filters) {
        this.filters = asList(filters);
    }

    public static <T> EntityMapper<T> forClass(Class<T> clazz, Filter... filters) {
        return new SingleEntityMapper<T>(clazz, filters);
    }

    public static <T> EntityMapper<T> forSubclasses(SubclassChooser<T> chooser, Filter... filters) {
        return new SubclassesEntityMapper<T>(chooser, filters);
    }
}
