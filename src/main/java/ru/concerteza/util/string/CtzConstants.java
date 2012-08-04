package ru.concerteza.util.string;

import java.nio.charset.Charset;

/**
 * Common constants
 *
 * @author alexey
 * Date: 4/27/11
 */
public class CtzConstants {
    // common
    public static final String EMPTY_STRING = "";

    // encoding
    public static final String UTF8 = "UTF-8";
    public static final Charset UTF8_CHARSET = Charset.forName(UTF8);
    public static final String WINDOWS1251 = "windows-1251";
    public static final Charset WINDOWS1251_CHARSET = Charset.forName(WINDOWS1251);
    public static final String ASCII = "ASCII";
    public static final Charset ASCII_CHARSET = Charset.forName(ASCII);
    public static final String UTF16 = "UTF-16";
    public static final Charset UTF16_CHARSET = Charset.forName(UTF16);
}
