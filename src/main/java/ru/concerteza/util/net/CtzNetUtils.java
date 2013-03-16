package ru.concerteza.util.net;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import ru.concerteza.util.string.CtzStringUtils;

import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Network utilities
 *
 * @author wmel
 * Date: 29.03.11
 * @see CtzNetUtilsTest
 */
public class CtzNetUtils {
    private static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.[\\d]{1,3}$");
    private static final Splitter SPLITTER = Splitter.on('.');

    /**
     * Converts IPv4 address from string into long
     *
     * @param ipStr IPv4 address as string
     * @return IPv4 address as long
     */
    @Deprecated // use parseIpV4
    public static Long convertIpToLong(String ipStr) {
        checkArgument(isNotBlank(ipStr), "Provided Ip string mst be not blank");
        checkArgument(IP_PATTERN.matcher(ipStr).matches(), "Ip address must be in 000.000.000.000 format, but was: %s", ipStr);
        List<String> arr = ImmutableList.copyOf(SPLITTER.split(ipStr));
        return Long.valueOf(arr.get(0)) * 16777216 + Long.valueOf(arr.get(1)) * 65536 + Long.valueOf(arr.get(2)) * 256 + Long.parseLong(arr.get(3));
    }

    /**
     * Converts IPv4 address from long into string
     *
     * @param ip IPv4 address as long
     * @return IPv4 address as string
     */
    @Deprecated // use convertIpV4
    public static String convertIpToString(Long ip) {
        StringBuilder sb = new StringBuilder();
        sb.append((int) (ip / 16777216)).append(".")
                .append((int) (ip % 16777216 / 65536)).append(".")
                .append((int) (ip % 65536 / 256)).append(".")
                .append((int) (ip % 256));
        return sb.toString();
    }

    public static String convertIpV4(int ip) {
        return new StringBuilder()
                .append((ip >>> 24) & 0xFF).append(".")
                .append((ip >>> 16) & 0xFF).append(".")
                .append((ip >>> 8) & 0xFF).append(".")
                .append((ip >>> 0) & 0xFF)
                .toString();
    }

    public static int parseIpV4(String str) {
        Iterator<String> it = SPLITTER.split(str).iterator();
        int val = 0;
        for(int i = 0; i < 4; i++) {
            val <<= 8;
            val |= Integer.parseInt(it.next());
        }
        return val;
    }
}
