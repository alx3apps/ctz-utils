package ru.concerteza.util.option;


import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Implementation of <a href="http://www.codecommit.com/blog/scala/the-option-pattern">Option pattern</a>,
 * for wrapping nullable return values to force client-side null-check.
 * Instances may be either {@link Some} or {@link None}
 *
 * @author alexey,
 * Date: 8/15/11
 * @see Some
 * @see None
 */
@Deprecated // use guava's Optional
public abstract class Option<T> {
    private static final None NONE = new None();

    /**
     * Returns not null value from {@link Some}
     * @return not null value
     */
    public abstract T get();

    /**
     * Returns not null value from {@link Some} or provided default value from {@link None}
     * @param defaultValue value to return from {@link None}
     * @return value from {@link Some} or provided default value from {@link None}
     */
    public abstract T getIfAny(T defaultValue);

    /**
     * Positive null check
     * @return true for {@link None}, false for {@link Some}
     */
    public abstract boolean isNone();

    /**
     * Negative null check
     * @return  false for {@link None}, true for {@link Some}
     */
    public abstract boolean isSome();

    /**
     * Wraps non-null value into {@link Some}
     * @param t non-null value of type T
     * @param <T> type of wrapped value
     * @return {@link Some} containing provided value
     */
    public static <T> Some<T> some(T t) {
        return new Some<T>(t);
    }

    /**
     * Returns {@link None}
     * @param <T> not used
     * @return {@link None}
     */
    @SuppressWarnings("unchecked")
    public static <T> None<T> none() {
        return NONE;
    }

    /**
     * Wraps nullable value to {@link None} or {@link Some}
     * @param t nullable value
     * @param <T> value type
     * @return {@link None} if value is null, {@link Some} otherwise
     */
    public static <T> Option<T> wrapNull(T t) {
        return null == t ? new None<T>() : new Some<T>(t);
    }

    /**
     * Wraps string that may be empty
     * @param str string,  that may be empty
     * @return {@link None} if string is empty, {@link Some} otherwise
     */
    public static Option<String> wrapEmpty(String str) {
        return isEmpty(str) ? new None<String>() : new Some<String>(str);
    }

    /**
     * Calls {@code equals} on {@link Some} objects, return {@code false} otherwise
     * @param o object to check equality
     * @return see {@link OptionTest#testEquals}
     */
    @Override
    public boolean equals(Object o) {
        if(this.isNone()) return false;
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option other = (Option) o;
        return get().equals(other.get());
    }

    /**
     * {@code hashCode()}implementation
     * @return value's {@code hashCode()} result on {@link Some}, {@link Object#hashCode()} result on {@link None}
     */
    @Override
    public int hashCode() {
        return this.isNone() ? super.hashCode() : get().hashCode();
    }
}
