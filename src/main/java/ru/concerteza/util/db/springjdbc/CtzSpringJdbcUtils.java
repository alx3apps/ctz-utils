package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.concerteza.util.CtzReflectionUtils;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static ru.concerteza.util.CtzReflectionUtils.collectFields;

/**
 * User: alexey
 * Date: 10/15/11
 */
public class CtzSpringJdbcUtils {
    // 2x speed improvement over naive jt inserts
    @SuppressWarnings("unchecked")
    public static long insertBatch(NamedParameterJdbcTemplate jt,
                                   String sql, Iterator<Map<String, Object>> paramsIter, int batchSize) {
        List<Map<String, Object>> parList = new ArrayList<Map<String, Object>>(batchSize);
        boolean hasInfoFromDb = true;
        Map<String, Object>[] parArray = new Map[batchSize];
        long updated = 0;
        long counter = 0;
        while (paramsIter.hasNext()) {
            counter += 1;
            parList.add(paramsIter.next());
            if(0 == parList.size() % batchSize) {
                long up = doBatch(jt, sql, parList, parArray);
                if(hasInfoFromDb) {
                    if(-1 == up) hasInfoFromDb = false;
                    updated += up;
                }
            }
        }
        // tail
        if(parList.size() > 0) {
            Map<String, Object>[] partParArray = new Map[parList.size()];
            long up = doBatch(jt, sql, parList, partParArray);
            if (hasInfoFromDb) {
                if(-1 == up) hasInfoFromDb = false;
                updated += up;
            }
        }
        // check actually updated rows count, if db returns such info
        if(hasInfoFromDb) checkState(counter == updated, "Updated rows count reported by db differs from" +
                "count of rows sent for batch insert, expected: %s, db reports: %s", counter, updated);
        return counter;
    }

    // returns -1 on no info from db
    private static long doBatch(NamedParameterJdbcTemplate jt, String sql, List<Map<String, Object>> parList,
                                 Map<String, Object>[] parArray) {
        parList.toArray(parArray);
        int[] res = jt.batchUpdate(sql, parArray);
        parList.clear();
        return countUpdatedRows(res);
    }

    // returns -1 on no info from db
    private static long countUpdatedRows(int[] dbReturned) {
        long res = 0;
        for(int updated : dbReturned) {
            if(updated < 0) return -1;
            res += updated;
        }
        return res;
    }

    public Map<String, Field> createColumnMap(Class<?> clazz) {
        ImmutableMap.Builder<String, Field> builder = ImmutableMap.builder();
        for(Field fi : collectFields(clazz, new ColumnMapPredicate())){
            String key = fi.getAnnotation(Column.class).name();
            if(null == key) key = fi.getName();
            builder.put(key, fi);
        }
        return builder.build();
    }

    private class ColumnMapPredicate implements Predicate<Field> {
        @Override
        public boolean apply(Field fi) {
            return fi.isAnnotationPresent(Column.class);
        }
    }
}
