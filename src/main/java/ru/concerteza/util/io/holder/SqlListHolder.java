package ru.concerteza.util.io.holder;

import ru.concerteza.util.string.CtzConstants;

import javax.annotation.PostConstruct;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.concerteza.util.io.SqlListParser.parseToMap;

/**
 * Abstract class for parsing JSON map files (e.g. with SQL queries)
 *
 * @author alexey
 * Date: 6/25/12
 */
public abstract class SqlListHolder {
    private Map<String, String> queries;

    /**
     * Should be called after inheritors fields injection or manually without spring
     */
    @PostConstruct
    protected void postConstruct() {
        this.queries = parseToMap(sqlFilePath(), encoding());
    }

    /**
     * @param key map key
     * @return value registered for provided key
     * @throws IllegalArgumentException on unknown key
     */
    public String get(String key) {
        String res = queries.get(key);
        checkArgument(null != res, "No queries for key: %s, existed keys: %s", key, queries.keySet());
        return res;
    }

    protected abstract String sqlFilePath();

    protected String encoding() {
        return CtzConstants.UTF8;
    }
}
