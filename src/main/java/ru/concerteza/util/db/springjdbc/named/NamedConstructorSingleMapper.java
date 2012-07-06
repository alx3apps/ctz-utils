package ru.concerteza.util.db.springjdbc.named;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * User: alexey
 * Date: 7/6/12
 */
class NamedConstructorSingleMapper<T> extends NamedConstructorMapper<T> {
    private final NamedConstructorFunction<T> fun;

    NamedConstructorSingleMapper(NamedConstructorFunction<T> fun) {
        this.fun = fun;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, ?> dataMap = mapper.mapRow(rs, rowNum);
        return fun.apply(dataMap);
    }
}
