package ru.concerteza.util.io.holder;

import ru.concerteza.util.string.CtzConstants;

import javax.annotation.PostConstruct;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.concerteza.util.io.SqlListParser.parseToMap;

/**
 * Abstract class for parsing SQL list files into name->query maps
 *
 * @author alexey
 * Date: 6/25/12
 */
@Deprecated // not useful, use app code
public abstract class SqlMapHolder {
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
        checkArgument(null != res, "No queries for key: '%s', existed keys: '%s'", key, queries.keySet());
        return res;
    }

    /**
     * @return SQL file spring resource path
     */
    protected abstract String sqlFilePath();

    /**
     * @return resource encoding, UTF-8 by default
     */
    protected String encoding() {
        return CtzConstants.UTF8;
    }
}
