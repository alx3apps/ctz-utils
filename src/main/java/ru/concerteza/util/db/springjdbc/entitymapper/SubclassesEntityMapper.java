package ru.concerteza.util.db.springjdbc.entitymapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.concerteza.util.CtzReflectionUtils.mapToObject;

/**
 * Implementation of {@link EntityMapper} for entity hierarchies
 *
 * @author alexey
 * Date: 5/16/12
 * @see EntityMapper
 * @see Filter
 * @see SubclassChooser
 */
class SubclassesEntityMapper<T> extends EntityMapper<T> {
    private final SubclassChooser<T> chooser;
    private final Map<Class<? extends T>, Map<String, Field>> classColumnMap;

    /**
     * Main constructor
     * @param chooser {@link SubclassChooser} defining what class to instantiate
     * @param classColumnMap column_name->class_field map for each entity in hierarchy
     * @param filters vararg {@link Filter}'s that will be applied to row data before mapping
     */
    protected SubclassesEntityMapper(SubclassChooser<T> chooser, Map<Class<? extends T>, Map<String, Field>> classColumnMap, Filter... filters) {
        super(filters);
        this.chooser = chooser;
        this.classColumnMap = classColumnMap;
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
        for (Filter fi : filters) dataMap = fi.apply(dataMap);
        Class<? extends T> clazz = chooser.choose(dataMap);
        Map<String, Field> columnMap = classColumnMap.get(clazz);
        checkArgument(null != columnMap, "Cannot find column map for chosen subclass: '%s', subclasses: '%s'", clazz, classColumnMap.keySet());
        return mapToObject(dataMap, clazz, columnMap);
    }
}
