package ru.concerteza.util.db.springjdbc.named;

import com.google.common.collect.Maps;
import ru.concerteza.util.db.springjdbc.RowIterable;
import ru.concerteza.util.reflect.named.NamedConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.concerteza.util.reflect.named.NamedConstructor.CaseType;
import static ru.concerteza.util.reflect.named.NamedConstructor.CaseType.INSENSITIVE;
import static ru.concerteza.util.reflect.named.NamedConstructor.MatchMode.ADDITIONAL_ALLOWED;
import static ru.concerteza.util.string.CtzFormatUtils.format;

/**
 * Named constructor implementation for class hierarchy mapping. Converts result set row into lower case map,
 * chooses function by discriminator column value and applies it.
 *
 * @author alexey
 * Date: 7/6/12
 * @see NamedConstructorMapper
 * @see ru.concerteza.util.reflect.named.NamedConstructor
 * @see NamedConstructor_OLD
 */
class NamedConstructorSubclassesMapper<T> extends NamedConstructorMapper<T> {
    private final String discColumn;
    private final Map<String, NamedConstructor<? extends T>> ncMap;

    /**
     * @param ncMap discriminator value -> named constructor function mapping
     * @param discColumn discriminator column
     */
    NamedConstructorSubclassesMapper(Map<String, NamedConstructor<? extends T>> ncMap, String discColumn) {
        checkArgument(ncMap.size() > 0, "Provided functions map is empty");
        checkArgument(isNotBlank(discColumn), "Provided discriminator column is blank");
        this.ncMap = ncMap;
        this.discColumn = discColumn.toLowerCase(Locale.ENGLISH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<String, Object> map = Maps.newHashMap();
        String discVal = null;
        for(RowIterable.Cell cell : RowIterable.of(rs)) {
            if(discColumn.equalsIgnoreCase(cell.getColumnName())) discVal = (String) cell.getValue();
            else map.put(cell.getColumnName().toLowerCase(Locale.ENGLISH), cell.getValue());
        }
        if(null == discVal) throw new IllegalArgumentException(format(
                "Null or absent value of discriminator column: '{}' in row data: '{}'", discColumn, logRS(rs, rowNum)));
        NamedConstructor<? extends T> nc = ncMap.get(discVal);
        if(null == nc) throw new IllegalArgumentException(format(
                "Cannot find subclass for discriminator: '{}', keys: '{}', row data: '{}'", discVal, ncMap.keySet(), logRS(rs, rowNum)));
        return nc.invoke(map, ADDITIONAL_ALLOWED, INSENSITIVE);
    }
}
