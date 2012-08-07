package ru.concerteza.util.db.springjdbc.named;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import ru.concerteza.util.db.springjdbc.RowIterable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;

import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * Named constructor mapper implementation for single class.
 * Converts result set row into lower case map and applies {@link NamedConstructorFunction} to it.
 *
 * @author alexey
 * Date: 7/6/12
 * @see NamedConstructorMapper
 * @see NamedConstructorFunction
 * @see NamedConstructor
 */
class NamedConstructorSingleMapper<T> extends NamedConstructorMapper<T> {
    private final NamedConstructorFunction<T> fun;

    /**
     * @param fun function to apply to row
     */
    NamedConstructorSingleMapper(NamedConstructorFunction<T> fun) {
        this.fun = fun;
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
        return fun.apply(map);
    }
}
