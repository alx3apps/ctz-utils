package ru.concerteza.util.option;


import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * User: alexey
 * Date: 8/15/11
 */
public abstract class Option<T> {
    private static final None NONE = new None();

    public abstract T get();

    public abstract T getIfAny(T defaultValue);

    public abstract boolean isNone();

    public abstract boolean isSome();

    public static <T> Some<T> some(T t) {
        checkNotNull(t, "Some value cannot be null");
        return new Some<T>(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> None<T> none() {
        return NONE;
    }

    public static <T> Option<T> wrapNull(T t) {
        return null == t ? new None<T>() : new Some<T>(t);
    }

    public static Option<String> wrapEmpty(String str) {
        return isEmpty(str) ? new None<String>() : new Some<String>(str);
    }
}
