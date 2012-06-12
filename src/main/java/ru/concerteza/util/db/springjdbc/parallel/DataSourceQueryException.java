package ru.concerteza.util.db.springjdbc.parallel;

import org.springframework.dao.DataAccessException;

import static java.lang.Thread.currentThread;
import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 6/12/12
 */
class DataSourceQueryException extends DataAccessException {
    public DataSourceQueryException(Throwable cause) {
        super(format("Thread: '{}', message: {}", currentThread().getName(), cause.getMessage()), cause);
    }
}
