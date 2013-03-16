package ru.concerteza.util.io.holder;

import org.springframework.core.io.Resource;
import ru.concerteza.util.string.CtzConstants;

import javax.annotation.PostConstruct;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.concerteza.util.json.CtzJsonUtils.parseStringMap;
import static ru.concerteza.util.io.CtzResourceUtils.RESOURCE_LOADER;

/**
 * Abstract class for parsing JSON map files (e.g. with SQL queries)
 *
 * @author alexey
 * Date: 6/25/12
 * @see JsonMapHolderTest
 */
@Deprecated // not useful, use app code
public abstract class JsonMapHolder {
    private Map<String, String> queries;

    /**
     * Should be called after inheritors fields injection or manually without spring
     */
    @PostConstruct
    protected void postConstruct() {
        Resource resource = RESOURCE_LOADER.getResource(jsonFilePath());
        this.queries = parseStringMap(resource);
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

    /**
     * @return spring resource path to json file containing string->string map
     */
    protected abstract String jsonFilePath();

    /**
     * @return resource encoding, UTF-8 by default
     */
    protected String encoding() {
        return CtzConstants.UTF8;
    }
}
