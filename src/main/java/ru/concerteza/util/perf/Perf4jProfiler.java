package ru.concerteza.util.perf;

import com.google.common.collect.Maps;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.perf4j.StopWatch;
import org.perf4j.helpers.StatsValueRetriever;
import org.perf4j.log4j.AsyncCoalescingStatisticsAppender;
import org.perf4j.log4j.GraphingStatisticsAppender;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * User: alexey
 * Date: 10/9/12
 */
public class Perf4jProfiler {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Perf4jProfiler.class);
    private static final org.apache.log4j.Logger perf4j = Logger.getLogger("org.perf4j");

    private static final Map<String, StopWatch> map = Maps.newHashMap();
    private static final Object lock = new Object();

    // http://perf4j.codehaus.org/devguide.html#Using_the_log4j_Appenders_to_Generate_Real-Time_Performance_Information
    public static void configure(long sliceMillis) {
        // extract downstream
        if(perf4j.isInfoEnabled()) {
            logger.info("Enabling perf4j profiling...");
            // log4j magic here
            perf4j.setAdditivity(false);
            perf4j.setLevel(Level.INFO);
            // create coalescing
            AsyncCoalescingStatisticsAppender coalescing = new AsyncCoalescingStatisticsAppender();
            coalescing.setName("coalescing");
            coalescing.setDownstreamLogLevel(Level.INFO.toString());
            coalescing.setTimeSlice(sliceMillis);
            perf4j.addAppender(coalescing);
            // create graph
            // mean
            GraphingStatisticsAppender mean = new GraphingStatisticsAppender();
            mean.setGraphType(StatsValueRetriever.MEAN_VALUE_RETRIEVER.getValueName());
            mean.setName("mean");
            coalescing.addAppender(mean);
            mean.activateOptions();
            // stddev
            GraphingStatisticsAppender stddev = new GraphingStatisticsAppender();
            stddev.setGraphType(StatsValueRetriever.STD_DEV_VALUE_RETRIEVER.getValueName());
            stddev.setName("stddev");
            coalescing.addAppender(stddev);
            stddev.activateOptions();
            // min
            GraphingStatisticsAppender min = new GraphingStatisticsAppender();
            min.setName("min");
            min.setGraphType(StatsValueRetriever.MIN_VALUE_RETRIEVER.getValueName());
            coalescing.addAppender(min);
            min.activateOptions();
            // max
            GraphingStatisticsAppender max = new GraphingStatisticsAppender();
            max.setName("max");
            max.setGraphType(StatsValueRetriever.MAX_VALUE_RETRIEVER.getValueName());
            coalescing.addAppender(max);
            max.activateOptions();
            // count
            GraphingStatisticsAppender count = new GraphingStatisticsAppender();
            count.setName("count");
            count.setGraphType(StatsValueRetriever.COUNT_VALUE_RETRIEVER.getValueName());
            coalescing.addAppender(count);
            count.activateOptions();
            // tps
            GraphingStatisticsAppender tps = new GraphingStatisticsAppender();
            tps.setName("tps");
            tps.setGraphType(StatsValueRetriever.TPS_VALUE_RETRIEVER.getValueName());
            coalescing.addAppender(tps);
            tps.activateOptions();

            coalescing.activateOptions();
        } else {
            logger.info("Profiling is disabled");
        }
    }


    // these methods are for cross-thread profiling,
    // for spring public methods profiling use @Profiled
    public static void startWatch(String key) {
        if(disabled()) return;
        checkArgument(isNotEmpty(key), "Stopwatch key must be non empty");
        synchronized(lock) {
            checkState(!map.containsKey(key), "StopWatch map: '%s' already contains key: '%s'", map, key);
            StopWatch watch = new Slf4JStopWatch(key);
            map.put(key, watch);
        }
    }

    public static void lapWatch(String key) {
        if(disabled()) return;
        checkArgument(isNotEmpty(key), "Stopwatch key must be non empty");
        synchronized(lock) {
            StopWatch watch = map.remove(key);
            checkState(null != watch, "StopWatch map: '%s' doesn't contain key: '%s'", map, key);
            watch.lap(key);
        }
    }

    public static void stopWatch(String key) {
        if(disabled()) return;
        checkArgument(isNotEmpty(key), "Stopwatch key must be non empty");
        synchronized(lock) {
            StopWatch watch = map.remove(key);
            checkState(null != watch, "StopWatch map: '%s' doesn't contain key: '%s'", map, key);
            watch.stop();
        }
    }

    public static void log(long start, long time, String tag) {
        if(disabled()) return;
        StringBuilder sb = new StringBuilder("start[")
                .append(start)
                .append("] time[")
                .append(time)
                .append("] tag[")
                .append(tag)
                .append("]");
        perf4j.info(sb.toString());
    }

    public static boolean disabled() {
        return !perf4j.isInfoEnabled();
    }

    public static boolean enabled() {
        return perf4j.isInfoEnabled();
    }
}
