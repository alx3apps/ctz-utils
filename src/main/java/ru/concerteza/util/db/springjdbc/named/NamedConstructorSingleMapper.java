package ru.concerteza.util.db.springjdbc.named;

import com.google.common.collect.Maps;
import ru.concerteza.util.db.springjdbc.RowIterable;
import ru.concerteza.util.reflect.named.NamedConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;

import static ru.concerteza.util.reflect.named.NamedConstructor.CaseType.INSENSITIVE;
import static ru.concerteza.util.reflect.named.NamedConstructor.MatchMode.ADDITIONAL_ALLOWED;

/**
 * Named constructor mapper implementation for single class.
 * Converts result set row into lower case map and applies {@link ru.concerteza.util.reflect.named.NamedConstructor} to it.
 *
 * @author alexey
 * Date: 7/6/12
 * @see NamedConstructorMapper
 * @see ru.concerteza.util.reflect.named.NamedConstructor
 * @see NamedConstructor_OLD
 */
class NamedConstructorSingleMapper<T> extends NamedConstructorMapper<T> {
    private final NamedConstructor<T> nc;

    /**
     * @param nc function to apply to row
     */
    NamedConstructorSingleMapper(NamedConstructor<T> nc) {
        this.nc = nc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<String, Object> map = Maps.newHashMap();
        for(RowIterable.Cell cell : RowIterable.of(rs)) {
            map.put(cell.getColumnName().toLowerCase(Locale.ENGLISH), cell.getValue());
        }
        return nc.invoke(map, ADDITIONAL_ALLOWED, INSENSITIVE);
    }
}
