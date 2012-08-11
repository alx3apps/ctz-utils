package ru.concerteza.util.db.springjdbc.named;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: alexey
 * Date: 8/10/12
 */

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface NamedGenericRef {
    /** The name. */
    String name();

    Class<?> type();
}
