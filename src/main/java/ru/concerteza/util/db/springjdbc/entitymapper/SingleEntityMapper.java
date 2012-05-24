package ru.concerteza.util.db.springjdbc.entitymapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static ru.concerteza.util.CtzReflectionUtils.columnFieldMap;
import static ru.concerteza.util.CtzReflectionUtils.mapToObject;

/**
 * Implementation of {@link EntityMapper} for single entities
 *
 * @author alexey
 * Date: 5/16/12
 * @see EntityMapper
 * @see Filter
 */
class SingleEntityMapper<T> extends EntityMapper<T> {
    private final Class<T> clazz;
    private final Map<String, Field> columnMap;

    /**
     * Main constructor
     * @param clazz entity class to instantiate
     * @param columnMap column_name->class_field map
     * @param filters vararg {@link Filter}'s that will be applied to row data before mapping
     */
    protected SingleEntityMapper(Class<T> clazz, Map<String, Field> columnMap, Filter... filters) {
        super(filters);
        this.clazz = clazz;
        this.columnMap = columnMap;
    }

    /**
	 * Implementations must implement this method to map each row of data
	 * in the ResultSet. This method should not call <code>next()</code> on
	 * the ResultSet; it is only supposed to map values of the current row.
	 * @param rs the ResultSet to map (pre-initialized for the current row)
	 * @param rowNum the number of the current row
	 * @return the result object for the current row
	 * @throws SQLException if a SQLException is encountered getting
	 * column values (that is, there's no need to catch SQLException)
	 */
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> dataMap = mapper.mapRow(rs, rowNum);
        for(Filter fi : filters) dataMap = fi.apply(dataMap);
        return mapToObject(dataMap, clazz, columnMap);
    }
}
