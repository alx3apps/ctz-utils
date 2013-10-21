package ru.concerteza.util.number;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import javax.annotation.Nonnull;
import ru.concerteza.util.except.IllegalArgumentTypeException;

/**
 * Operations on {@link Number}.
 *
 * @author  Timofey Gorshkov
 * created 25.09.2013
 */
public class CtzNumberUtils {
    private CtzNumberUtils() {}

    /**
     * Converts {@code Number} argument to an {@code int}, checking for lost information.
     * If argument has a nonzero fractional part or is out of the possible range for an {@code int} result
     * then an {@code ArithmeticException} is thrown.
     * <p>
     * This method supports {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     * {@link java.math.BigInteger} and {@link java.math.BigDecimal} argument types. Argument of another type
     * will cause {@code IllegalArgumentTypeException}.
     *
     * @param number {@link Number} value to be converted to {@code int}.
     * @return {@code number} converted to an {@code int}.
     * @throws ArithmeticException if {@code number} has a nonzero fractional part, or will not fit in an {@code int}.
     * @throws IllegalArgumentTypeException if {@code number} is of unsupported type.
     */
    public static int intValueOf(@Nonnull Number number) {
        Class clazz = number.getClass();
        // IntValueFunction function = IntValueFunction.over(clazz); // is Oracle JDK 1.6 javac incompatible
        Function<Number, Integer> function = IntValueFunction.over(clazz);
        return function.apply(number);
    }

    /** @see CtzNumbers#intValueOf */
    private static enum IntValueFunction implements Function<Number, Integer> {
        FROM_INT { @Override public Integer apply(Number number) {
            return number.intValue();
        }},
        FROM_LONG { @Override public Integer apply(Number number) {
            long num = number.longValue();
            if ((int)num != num) throw new ArithmeticException("Overflow");
            return (int)num;
        }},
        FROM_DOUBLE { @Override public Integer apply(Number number) {
            double num = number.doubleValue();
            if (num % 1 != 0) throw new ArithmeticException("Rounding necessary");
            if ((int)num != num) throw new ArithmeticException("Overflow");
            return (int)num;
        }},
        FROM_BIG_INTEGER { @Override public Integer apply(Number number) {
            return (new BigDecimal((BigInteger)number)).intValueExact();
        }},
        FROM_BIG_DECIMAL { @Override public Integer apply(Number number) {
            return ((BigDecimal)number).intValueExact();
        }};

        private static final Map<Class<? extends Number>, IntValueFunction> MAP
                = ImmutableMap.<Class<? extends Number>, IntValueFunction>builder()
                .put(Byte.class, FROM_INT)
                .put(Short.class, FROM_INT)
                .put(Integer.class, FROM_INT)
                .put(Long.class, FROM_LONG)
                .put(Float.class, FROM_DOUBLE)
                .put(Double.class, FROM_DOUBLE)
                .put(BigInteger.class, FROM_BIG_INTEGER)
                .put(BigDecimal.class, FROM_BIG_DECIMAL)
                .build();

        static IntValueFunction over(Class<? extends Number> numberClass) {
            IntValueFunction function = MAP.get(numberClass);
            if (function != null) return function;
            throw new IllegalArgumentTypeException(numberClass);
        }
    }
}
