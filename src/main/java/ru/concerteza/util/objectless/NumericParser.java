package ru.concerteza.util.objectless;

import static java.lang.Character.digit;
import static java.lang.Character.isWhitespace;
import static java.lang.Math.min;
import static ru.concerteza.util.string.CtzConstants.ASCII_CHARSET;

/**
 * Parses numbers from byte[] strings without creating intermediate String objects.
 * Does not support negative numbers.
 * Uses assertions for number checks.
 *
 * @author alexkasko
 * Date: 4/18/13
 */
public class NumericParser {
    /**
     * Parse long from byte[] string
     *
     * @param numStr input string
     * @return long value
     */
    public static long parseLong(byte[] numStr) {
        return parseLong(numStr, 0, numStr.length, 10);
    }

    /**
     * Parse long from byte[] string
     *
     * @param numStr input string
     * @param offset number offset in string
     * @param length number length in string
     * @return long value
     */
    public static long parseLong(byte[] numStr, int offset, int length) {
        return parseLong(numStr, offset, length, 10);
    }

    /**
     * Parse long from byte[] string
     *
     * @param numStr input string
     * @param offset number offset in string
     * @param length number length in string
     * @param radix number radix
     * @return long value
     */
    public static long parseLong(byte[] numStr, int offset, int length, int radix) {
        assert numStr != null : null;
        assert radix >= Character.MIN_RADIX : "radix " + radix + " less than Character.MIN_RADIX";
        assert radix <= Character.MAX_RADIX : "radix " + radix + " greater than Character.MAX_RADIX";
        assert length > 0 : "empty input";
        long result = 0;
        int i = 0;
        long limit = -Long.MAX_VALUE;
        long multmin;
        int digit;
        multmin = limit / radix;
        while (i < length) {
            char ch = (char) numStr[offset + i++];
            // null-terminated or whitespace-terminated string
            if('\0' == ch || isWhitespace(ch)) break;
            // Accumulating negatively avoids surprises near MAX_VALUE
            digit = digit(ch, radix);
            assert digit >= 0 : new String(numStr, ASCII_CHARSET);
            assert result >= multmin : new String(numStr, ASCII_CHARSET);
            result *= radix;
            assert result >= limit + digit : new String(numStr, ASCII_CHARSET);
            result -= digit;
        }
        return -result;
    }

    /**
     * Parse int from byte[] string
     *
     * @param numStr input string
     * @return int value
     */
    public static int parseInt(byte[] numStr) {
        return parseInt(numStr, 0, numStr.length, 10);
    }

    /**
     * Parse int from byte[] string
     *
     * @param numStr input string
     * @param offset number offset in string
     * @param length number length in string
     * @return int value
     */
    public static int parseInt(byte[] numStr, int offset, int length) {
        return parseInt(numStr, offset, length, 10);
    }

    /**
     * Parse int from byte[] string
     *
     * @param numStr input string
     * @param offset number offset in string
     * @param length number length in string
     * @param radix number radix
     * @return int value
     */
    public static int parseInt(byte[] numStr, int offset, int length, int radix) {
        assert numStr != null : null;
        assert radix >= Character.MIN_RADIX : "radix " + radix + " less than Character.MIN_RADIX";
        assert radix <= Character.MAX_RADIX : "radix " + radix + " greater than Character.MAX_RADIX";
        assert length > 0 : "empty input";
        int result = 0;
        int i = 0;
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;
        multmin = limit / radix;
        while (i < length) {
            char ch = (char) numStr[offset + i++];
            // null-terminated or whitespace-terminated string
            if('\0' == ch || isWhitespace(ch)) break;
            // Accumulating negatively avoids surprises near MAX_VALUE
            digit = digit(ch, radix);
            assert digit >= 0 : new String(numStr, ASCII_CHARSET);
            assert result >= multmin : new String(numStr, ASCII_CHARSET);
            result *= radix;
            assert result >=  limit + digit : new String(numStr, ASCII_CHARSET);
            result -= digit;
        }
        return -result;
    }

    /**
     * Parse IP v.4 address in dot notation from byte[] string
     *
     * @param ipStr IP v.4 address
     * @return IP as long
     */
    public static long parseIpV4(byte[] ipStr) {
        return parseIpV4(ipStr, 0);
    }

    /**
     * Parse IP v.4 address in dot notation from byte[] string
     *
     * @param ipStr IP v.4 address
     * @param offset IP offset in string
     * @return IP as long
     */
    public static long parseIpV4(byte[] ipStr, int offset) {
        long val = 0;
        int start = offset;
        int len = 0;
        int max = min(ipStr.length - offset, 15);
        for (int i = 0; i < max; i++) {
            char ch = (char) ipStr[offset + i];
            // null-terminated or whitespace-terminated string
            if('\0' == ch || isWhitespace(ch)) break;
            if('.' == ch) {
                val |= parseLong(ipStr, start, len);
                val <<= 8;
                start += len + 1;
                len = 0;
            } else len +=1;

        }
        // tail
        val |= parseLong(ipStr, start, len);
        return val;
    }
}