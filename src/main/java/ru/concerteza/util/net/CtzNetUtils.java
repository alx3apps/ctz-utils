package ru.concerteza.util.net;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static ru.concerteza.util.string.CtzConstants.ASCII_CHARSET;

/**
 * Network utilities
 *
 * @author wmel
 * Date: 29.03.11
 * @see CtzNetUtilsTest
 */
public class CtzNetUtils {
    private static final Logger logger = LoggerFactory.getLogger(CtzNetUtils.class);
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
    @Deprecated // use printIpV4
    public static String convertIpToString(Long ip) {
        StringBuilder sb = new StringBuilder();
        sb.append((int) (ip / 16777216)).append(".")
                .append((int) (ip % 16777216 / 65536)).append(".")
                .append((int) (ip % 65536 / 256)).append(".")
                .append((int) (ip % 256));
        return sb.toString();
    }

    /**
     * Converts IP v.4 from long to string
     *
     * @param ip IP v.4 as long
     * @return IP v.4 as string
     */
    public static String printIpV4(long ip) {
        return new StringBuilder()
                .append((ip >>> 24) & 0xFF).append(".")
                .append((ip >>> 16) & 0xFF).append(".")
                .append((ip >>> 8) & 0xFF).append(".")
                .append((ip >>> 0) & 0xFF)
                .toString();
    }

    /**
     * Converts IP v.4 from string to long
     *
     * @param str IP v.4 as string
     * @return IP v.4 as long
     */
    public static int parseIpV4(String str) {
        Iterator<String> it = SPLITTER.split(str).iterator();
        int val = 0;
        for(int i = 0; i < 4; i++) {
            val <<= 8;
            val |= Integer.parseInt(it.next());
        }
        return val;
    }

    /**
     * Read specified bytes count from stream using multiple read attempts if first read is not full
     *
     * @param stream stream to read
     * @param buf destination buffer
     * @param pos destination buffer
     * @param length number of bytes to read
     * @param wait millis to wait between subsequent read attempts
     * @param cycles max number of read cycles
     * @param name input stream name for error reporting purposes
     * @return number of actual read bytes
     * @throws IOException on max cycles excess or IO error
     */
    public static int readFromSlowStream(InputStream stream, byte[] buf, int pos, int length, long wait, int cycles, String name) throws IOException {
        int count = 0;
        for (int i = 0; i < cycles; i++) {
            int res = stream.read(buf, pos + count, length - count);
            if (-1 == res) {
                return 0 == count ? -1 : count;
            }
            count += res;
            if (count >= length) return count;
            if (i > 0) {
                logger.warn("Slow read of file: [{}], try num: [{}], bytes read: [{}]", new Object[]{name, i, count});
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    throw new IOException("Wait for next read from slow stream was interrupted, " +
                            "file: [" + name + "], try num: [" + i + "], bytes read: [" + count + "]", e);
                }
            }
        }
        throw new IOException("Stream read error, tries: [" + cycles + "], " +
                "count: [" + count + "], data: [" + new String(buf, 0, count, ASCII_CHARSET) + "]");
    }

    /**
     * Skips specified bytes count from stream using multiple read attempts if first read is not full
     *
     * @param stream stream to read
     * @param length number of bytes to read
     * @param wait millis to wait between subsequent read attempts
     * @param cycles max number of read cycles
     * @param name input stream name for error reporting purposes
     * @return number of actual read bytes
     * @throws IOException on max cycles excess or IO error
     */
    public static long skipFromSlowStream(InputStream stream, long length, long wait, int cycles, String name) throws IOException {
        long count = 0;
        for (int i = 0; i < cycles; i++) {
            long res = stream.skip(length - count);
            if (-1 == res) {
                return 0 == count ? -1 : count;
            }
            count += res;
            if (count >= length) return count;
            if (i > 0) {
                logger.warn("Slow skip of stream name: [{}], tries num: [{}], bytes skipped: [{}]", new Object[]{name, i, count});
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    throw new IOException("Wait for next skip from slow stream was interrupted, " +
                        "file: [" + name + "], try num: [" + i + "], bytes skipped: [" + count + "]", e);
                }
            }
        }
        throw new IOException("Stream skip error, tries: [" + cycles + "], " +
                "count: [" + count + "], name: [" + name + "]");
    }
}
