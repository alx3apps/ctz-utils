package ru.concerteza.util.db.springjdbc.named;

import java.lang.reflect.Constructor;
import java.util.LinkedHashSet;

/**
 * User: alexey
 * Date: 8/11/12
 */
class NamedConstructor<T> {
    final Constructor<T> constr;
    final LinkedHashSet<NCArg> args;

    public NamedConstructor(Constructor<T> constr, LinkedHashSet<NCArg> args) {
        this.constr = constr;
        this.args = args;
    }
}
