package ru.concerteza.util.db.springjdbc.named;

import com.google.common.collect.ImmutableMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * User: alexey
 * Date: 7/6/12
 */
class NamedConstructorSubclassesMapper<T> extends NamedConstructorMapper<T> {
    private final String discColumn;
    private final Map<String, NamedConstructorFunction<? extends T>> efMap;

    public NamedConstructorSubclassesMapper(String discColumn, Map<String, NamedConstructorFunction<? extends T>> efMap) {
        checkArgument(isNotBlank(discColumn), "Provided discriminator column is blank");
        checkNotNull(efMap, "Provided functions map is null");
        checkArgument(efMap.size() > 0, "Provided functions map is empty");
        this.discColumn = discColumn.toLowerCase();
        this.efMap = efMap;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, ?> dataMap = mapper.mapRow(rs, rowNum);
        String discVal = (String) dataMap.get(discColumn);
        checkArgument(null != discVal, "Cannot find disc column: '%s' in data map: '%s'", discColumn, dataMap);
        NamedConstructorFunction<? extends T> ef = efMap.get(discVal);
        checkArgument(null != ef, "Cannot find entry for discriminator: '%s', keys: '%s'", discVal, efMap.keySet());
        Map<String, ?> filtered = removeDisc(dataMap);
        return ef.apply(filtered);
    }

    private Map<String, ?> removeDisc(Map<String, ?> dataMap) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        for(Map.Entry<String, ?> en : dataMap.entrySet()) {
            if(discColumn.equals(en.getKey())) continue;
            builder.put(en.getKey(), en.getValue());
        }
        return builder.build();
    }
}
