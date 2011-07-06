package ru.concerteza.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * User: alexey
 * Date: 6/9/11
 */
public class CtzStringUtils {
    public static List<String> split(Splitter splitter, String str) {
        return ImmutableList.copyOf(splitter.split(str));
    }
}
