package ru.concerteza.util;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.regex.Pattern;

import static ru.concerteza.util.GuavaUtils.checkArg;
import static ru.concerteza.util.GuavaUtils.split;

/**
 * User: wmel
 * Date: 29.03.11
 */
public class IpAddressUtils {
    private static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.[\\d]{1,3}$");
    private static final Splitter SPLITTER = Splitter.on('.');

    public static Long convertIpToLong(String ipStr) {
        checkArg(IP_PATTERN.matcher(ipStr).matches(), "Ip address must be in 000.000.000.000 format, but was: {}", ipStr);
        List<String> arr = split(SPLITTER, ipStr);
        return Long.valueOf(arr.get(0)) * 16777216 + Long.valueOf(arr.get(1)) * 65536 + Long.valueOf(arr.get(2)) * 256 + Long.parseLong(arr.get(3));
    }

    public static String convertIpToString(Long ip) {
        StringBuilder sb = new StringBuilder();
        sb.append((int) (ip / 16777216)).append(".")
                .append((int) (ip % 16777216 / 65536)).append(".")
                .append((int) (ip % 65536 / 256)).append(".")
                .append((int) (ip % 256));
        return sb.toString();
    }
}
