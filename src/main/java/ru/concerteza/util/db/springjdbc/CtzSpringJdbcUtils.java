package ru.concerteza.util.db.springjdbc;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.System.arraycopy;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.concerteza.util.reflect.CtzReflectionUtils.collectFields;

/**
 * User: alexey
 * Date: 10/15/11
 */
public class CtzSpringJdbcUtils {

    // 2x speed improvement over naive jt inserts
    @SuppressWarnings("unchecked")
    public static long insertBatch(NamedParameterJdbcTemplate jt,
                                   String sql, Iterator<? extends SqlParameterSource> paramsIter, int batchSize) {
        return batchDml(jt, sql, paramsIter, batchSize, true);
    }


    public static long updateBatch(NamedParameterJdbcTemplate jt,
                                   String sql, Iterator<? extends SqlParameterSource> paramsIter, int batchSize) {
        return batchDml(jt, sql, paramsIter, batchSize, false);
    }

    private static long batchDml(NamedParameterJdbcTemplate jt, String sql,
                                 Iterator<? extends SqlParameterSource> paramsIter, int batchSize, boolean check) {

        checkNotNull(jt, "Provided JDBC Template is null");
        checkNotNull(paramsIter, "Provided parametricIter is null");
        checkArgument(isNotBlank(sql), "Provided SQL query is blank");
        checkArgument(batchSize > 0, "Batch size must be positive, but was: '%s'", batchSize);

        boolean hasInfoFromDb = true;
        // mutable for lower overhead
        SqlParameterSource[] params = new SqlParameterSource[batchSize];
        long updated = 0;
        long counter = 0;
        int index = 0;
        // main cycle
        while(paramsIter.hasNext()) {
            params[index] = paramsIter.next();
            index += 1;
            if(0 == index % batchSize) {
                int[] upArr = jt.batchUpdate(sql, params);
                if(hasInfoFromDb) {
                    long up = countUpdatedRows(upArr);
                    if(-1 == up) hasInfoFromDb = false;
                    updated += up;
                }
                index = 0;
            }
            counter += 1;
        }
        // tail
        if(index > 0) {
            SqlParameterSource[] partParArray = new SqlParameterSource[index];
            arraycopy(params, 0, partParArray, 0, index);
            int[] upArr = jt.batchUpdate(sql, partParArray);
            if(hasInfoFromDb) {
                long up = countUpdatedRows(upArr);
                if(-1 == up) hasInfoFromDb = false;
                updated += up;
            }
        }
        // check actually updated rows count, if db returns such info
        if(hasInfoFromDb && check) checkState(counter == updated, "Updated rows count reported by db differs from" +
                "count of rows sent for batch insert, expected: '%s', db reports: '%s'", counter, updated);
        return counter;
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
}
