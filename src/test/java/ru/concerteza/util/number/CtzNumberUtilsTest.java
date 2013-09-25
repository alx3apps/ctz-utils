package ru.concerteza.util.number;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author  Timofey Gorshkov
 * created 25.09.2013
 */
public class CtzNumberUtilsTest {
    public CtzNumberUtilsTest() {}

    private static void testIntValueOf(Number number, int expResult) {
        int result = CtzNumberUtils.intValueOf(number);
        assertEquals(expResult, result);
    }

    @Test
    public void testIntValueOfByte() {
        testIntValueOf((byte)-20, -20);
    }

    @Test
    public void testIntValueOfShort() {
        testIntValueOf((short)6446, 6446);
    }

    @Test
    public void testIntValueOfInteger() {
        testIntValueOf(-656339568, -656339568);
    }

    @Test
    public void testIntValueOfLong() {
        testIntValueOf(1372812388L, 1372812388);
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValueOfLongOverflow() {
        CtzNumberUtils.intValueOf(-5962025458789040912L);
    }

    @Test
    public void testIntValueOfFloat() {
        testIntValueOf(30227590F, 30227590);
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValueOfFloatRoundingNecessary() {
        CtzNumberUtils.intValueOf(-3862.892F);
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValueOfFloatOverflow() {
        CtzNumberUtils.intValueOf(77493005402734424.07F);
    }

    @Test
    public void testIntValueOfDouble() {
        testIntValueOf(-4822657.0, -4822657);
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValueOfDoubleRoundingNecessary() {
        CtzNumberUtils.intValueOf(75469.000001);
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValueOfDoubleOverflow() {
        CtzNumberUtils.intValueOf(-515320922892806.0);
    }

    @Test
    public void testIntValueOfBigInteger() {
        testIntValueOf(new BigInteger("1398819054"), 1398819054);
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValueOfBigIntegerOverflow() {
        CtzNumberUtils.intValueOf(new BigInteger("-573729220126282117534094344870"));
    }

    @Test
    public void testIntValueOfBigDecimal() {
        testIntValueOf(new BigDecimal("-471779180"), -471779180);
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValueOfBigDecimalRoundingNecessary() {
        CtzNumberUtils.intValueOf(new BigDecimal("627281.8111758"));
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValueOfBigDecimalOverflow() {
        CtzNumberUtils.intValueOf(new BigDecimal("82338753787716083601544396360201858"));
    }
}
