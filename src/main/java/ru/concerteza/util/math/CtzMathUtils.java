package ru.concerteza.util.math;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Math related utilities
 *
 * @author alexey
 * Date: 10/31/11
 */
@Deprecated // not useful
public class CtzMathUtils {
    public static final int DEFAULT_INT = -1;
    public static final BigDecimal DEFAULT_BIG_DECIMAL = BigDecimal.valueOf(-1);

    @Deprecated // not used
    public static int defaultInt(@Nullable BigDecimal input) {
        return defaultInt(input, DEFAULT_INT);
    }

    @Deprecated // not used
    public static int defaultInt(@Nullable BigDecimal input, int defaultValue) {
        return null == input ? defaultValue : input.intValueExact();
    }

    @Deprecated // not used
    public static int defaultInt(long input) {
        return defaultInt(input, DEFAULT_INT);
    }

    @Deprecated // not used
    public static int defaultInt(long input, int defaultValue) {
        return input >= Integer.MIN_VALUE && input <= Integer.MAX_VALUE ? (int) input : defaultValue;
    }

    @Deprecated // not used
    public static BigDecimal defaultBigDecimal(@Nullable BigDecimal input) {
        return defaultBigDecimal(input, DEFAULT_BIG_DECIMAL);
    }

    @Deprecated // not used
    public static BigDecimal defaultBigDecimal(@Nullable BigDecimal input, BigDecimal defaultValue) {
        return null == input ? defaultValue : input;
    }
}
