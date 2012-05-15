package ru.concerteza.util.db.springjdbc.filter;

import com.google.common.collect.ImmutableMap;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import ru.concerteza.util.db.springjdbc.JsonEntityMapper.Filter;

/**
 * @author Timofey Gorshkov
 * @since  2.3
 */
public class TimestampFilter implements Filter {

    private String[] tsFields;

    public TimestampFilter(String... tsFields) {
        this.tsFields = tsFields;
    }

    @Override
    public Map<String, Object> apply(Map<String, Object> input) {
        ImmutableMap.Builder<String, Object> res = ImmutableMap.builder();
        for (Map.Entry<String, Object> en : input.entrySet()) {
            res.put(en.getKey(), ArrayUtils.contains(tsFields, en.getKey())
                                 ? toTimestamp(en.getValue()) : en.getValue());
        }
        return res.build();
    }

    private Timestamp toTimestamp(Object value) {
        if (Timestamp.class.isAssignableFrom(value.getClass())) return (Timestamp)value;
        if (Date.class.isAssignableFrom(value.getClass())) return new Timestamp(((Date)value).getTime());
        if (value instanceof String) return Timestamp.valueOf((String)value);
        throw new IllegalArgumentException("Illegal argument class: " + value.getClass().getSimpleName());
    }
}
