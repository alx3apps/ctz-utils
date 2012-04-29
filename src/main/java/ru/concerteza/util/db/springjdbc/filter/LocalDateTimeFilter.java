package ru.concerteza.util.db.springjdbc.filter;

import com.google.common.collect.ImmutableMap;
import org.joda.time.LocalDateTime;
import ru.concerteza.util.date.CtzDateUtils;
import ru.concerteza.util.db.springjdbc.JsonEntityMapper;

import java.util.Date;
import java.util.Map;

/**
 * User: alexey
 * Date: 4/29/12
 */
public class LocalDateTimeFilter implements JsonEntityMapper.Filter {
    private String[] ldtFields;

    public LocalDateTimeFilter(String... ldtFields) {
        this.ldtFields = ldtFields;
    }

    @Override
    public Map<String, Object> apply(Map<String, Object> input) {
        ImmutableMap.Builder<String, Object> res = ImmutableMap.builder();
        for (Map.Entry<String, Object> en : input.entrySet()) {
            boolean untouched = true;
            for (String ldtFi : ldtFields) {
                if (ldtFi.equalsIgnoreCase(en.getKey()) && null != en.getValue()) {
                    LocalDateTime ldt = CtzDateUtils.toLocalDateTime((Date) en.getValue());
                    res.put(en.getKey(), ldt);
                    untouched = false;
                }
            }
            if(untouched) res.put(en.getKey(), en.getValue());
        }
        return res.build();
    }
}
