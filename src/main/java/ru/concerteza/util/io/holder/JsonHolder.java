package ru.concerteza.util.io.holder;

import ru.concerteza.util.string.CtzConstants;


/**
 * Supertype class for JSON files holders
 *
 * @author alexey
 * Date: 6/25/12
 */
public abstract class JsonHolder {
    /**
     * @return spring resource path to json file containing string->string map
     */
    protected abstract String jsonFilePath();

    protected String encoding() {
        return CtzConstants.UTF8;
    }
}
