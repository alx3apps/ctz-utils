package ru.concerteza.util.db.partition;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.concerteza.util.date.CtzDateUtils.toLocalDateTime;

/**
 * User: alexkasko
 * Date: 11/9/14
 */
public class PartitionManager {
    public static final Function<Partition, String> PARTITION_FULL_NAME_FUNCTION = new PartitionFullNameFun();
    private static final String DEFAULT_UID = "p";

    // filled it on init
    private final Map<String, ConcurrentLinkedQueue<Partition>> cache = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Partition>>();
    // prevents duplicate ddl
    private final Object mapLock = new Object();
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private final PartitionProvider provider;
    private final DateTimeFormatter fromFormat;
    private final DateTimeFormatter toFormat;
    private final Pattern splitPattern;
    private final ImmutableMap<String, Integer> tableStepMap;


    public PartitionManager(PartitionProvider provider, DateTimeFormatter fromFormat, DateTimeFormatter toFormat, Pattern splitPattern, ImmutableMap<String, Integer> tableStepMap) {
        this.provider = provider;
        this.fromFormat = fromFormat;
        this.toFormat = toFormat;
        this.splitPattern = splitPattern;
        this.tableStepMap = tableStepMap;
    }

    public ImmutableList<Partition> init() {
        ImmutableList.Builder<Partition> builder = ImmutableList.builder();
        for(String prefix : tableStepMap.keySet()) {
            ConcurrentLinkedQueue<Partition> parts = loadParts(prefix);
            this.cache.put(prefix, parts);
            builder.addAll(parts);
        }
        boolean firstTime = initialized.compareAndSet(false, true);
        if(!firstTime) throw new  PartitionException("Partition manager has already been initialized");
        return builder.build();
    }

    public Partition ensurePartition(String tableName, LocalDateTime date) {
        return ensurePartition(tableName, date, DEFAULT_UID);
    }

    public Partition ensurePartition(String tableName, LocalDateTime date, String uid) {
        return ensurePartition(tableName, date.toDate().getTime(), uid);
    }

    // no allocations allowed here outside of lock area
    public Partition ensurePartition(String tableName, long date, String uid) {
        if(!initialized.get()) throw new PartitionException("Partition manager has not been initialized");
        {
            final ConcurrentLinkedQueue<Partition> parts = cache.get(tableName);
            if(null != parts) {
                Partition res = null;
                for (Partition pa : parts) {
                    if (uid.equals(pa.getUid()) && pa.getFrom() <= date && date <= pa.getTo()) {
                        res = pa;
                    }
                }
                if (null != res) return res;
            }
        }
        synchronized (mapLock) {
            // double checking here
            final ConcurrentLinkedQueue<Partition> partsCheck = cache.get(tableName);
            final ConcurrentLinkedQueue<Partition> partsSync;
            Partition res = null;
            if (null == partsCheck) {
                partsSync = new ConcurrentLinkedQueue<Partition>();
                cache.put(tableName, partsSync);
            } else {
                partsSync = partsCheck;
                for (Partition pa : partsSync) {
                    if (uid.equals(pa.getUid()) && pa.getFrom() <= date && date <= pa.getTo()) {
                        res = pa;
                    }
                }
                if (null != res) return res;
            }
            // create new partition
            Partition part = createNewPartition(tableName, date, uid);
            partsSync.add(part);
            return part;
        }
    }

    public ImmutableList<String> tables() {
        return ImmutableList.copyOf(tableStepMap.keySet());
    }

    private ConcurrentLinkedQueue<Partition> loadParts(String prefix) {
        Collection<String> partStrings = provider.loadPartitions(prefix);
        ConcurrentLinkedQueue<Partition> parts = new ConcurrentLinkedQueue<Partition>();
        for(String st : partStrings) {
            Matcher ma = splitPattern.matcher(st);
            if(!ma.matches()) throw new PartitionException("Invalid partition name loaded from db: [" + st + "]");
            Map<String, String> groups = ma.namedGroups();
            LocalDateTime from = fromFormat.parseLocalDateTime(groups.get("from"));
            LocalDateTime toStepped = toFormat.parseLocalDateTime(groups.get("to"));
            LocalDateTime to = toStepped.withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999);
            String uid = groups.get("uid");
            Partition part = new Partition(fromFormat, toFormat, groups.get("name"), from.toDate().getTime(), to.toDate().getTime(), uid);
            parts.add(part);
        }
        return parts;
    }

    // slow operation
    private Partition createNewPartition(String tableName, long date, String uid) {
        Integer step = tableStepMap.get(tableName);
        if (null == step) throw new PartitionException("Invalid table name, refistered tables: [" + tableStepMap.keySet() + "]");
        if(0 != 24 % step) throw new PartitionException("24 must be divisible by step");
        LocalDateTime ldt = toLocalDateTime(date);
        LocalDateTime from = ldt.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0);
        while(from.getHourOfDay() % step > 0) from = from.minusHours(1);
        LocalDateTime to = from.plusHours(step).minusMillis(1);
        Partition part = new Partition(fromFormat, toFormat, tableName, from.toDate().getTime(), to.toDate().getTime(), uid);
        provider.createPartition(tableName, part.getPostfix());
        return part;
    }

    public static Builder builder(PartitionProvider provider) {
        return new Builder(provider);
    }

    public static class Builder {
        private final PartitionProvider provider;
        private String fromFormat = "yyyyMMddHH";
        private String toFormat = "yyyyMMddHH";
        private String splitPattern = "^(?<name>.+)_(?<from>\\d+)_(?<to>\\d+)_(?<uid>.+)$";
        private ImmutableMap.Builder<String, Integer> mapBuilder = ImmutableMap.builder();

        public Builder(PartitionProvider provider) {
            this.provider = provider;
        }

        public Builder withFromFormat(String format){
            this.fromFormat = format;
            return this;
        }

        public Builder withtoFormat(String format) {
            this.toFormat = format;
            return this;
        }

        public Builder withSplitPattern(String splitPattern) {
            this.splitPattern = splitPattern;
            return this;
        }

        public Builder withTable(String table, int step) {
            mapBuilder.put(table, step);
            return this;
        }

        public Builder withTables(int step, String... tables) {
            return withTables(step, ImmutableList.copyOf(tables));
        }

        public Builder withTables(int step, Collection<String> tables) {
            for (String ta : tables) {
                mapBuilder.put(ta, step);
            }
            return this;
        }

        public PartitionManager build() {
            DateTimeFormatter fromDft = DateTimeFormat.forPattern(fromFormat);
            DateTimeFormatter toDft = DateTimeFormat.forPattern(toFormat);
            Pattern pattern = Pattern.compile(splitPattern);
            return new PartitionManager(provider, fromDft, toDft, pattern, mapBuilder.build());
        }
    }

    public Finder finder(String table) {
        return new Finder(table);
    }

    public class Finder {
        private final String table;
        private Long fromDate;
        private Long toDate;
        private String uid;
        private Pattern uidPattern;

        public Finder(String table) {
            this.table = table;
        }

        public Finder withFromDate(long fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        public Finder withFromDate(LocalDateTime fromDate) {
            this.fromDate = fromDate.toDate().getTime();
            return this;
        }

        public Finder withToDate(long toDate) {
            this.toDate = toDate;
            return this;
        }

        public Finder withToDate(LocalDateTime toDate) {
            this.toDate = toDate.toDate().getTime();
            return this;
        }

        public Finder withUid(String uid) {
            this.uid = uid;
            return this;
        }

        public Finder withUidPattern(Pattern uidPattern) {
            this.uidPattern = uidPattern;
            return this;
        }

        public ImmutableList<Partition> find() {
            if(!initialized.get()) throw new PartitionException("Partition manager has not been initialized");
            ConcurrentLinkedQueue<Partition> parts = cache.get(table);
            if (null == parts) return ImmutableList.of();
            Iterator<Partition> iter = parts.iterator();
            if (null != fromDate) iter = Iterators.filter(iter, new FromDateFilter(fromDate));
            if (null != toDate) iter = Iterators.filter(iter, new ToDateFilter(toDate));
            if (null != uid) iter = Iterators.filter(iter, new UidFilter(uid));
            if (null != uidPattern) iter = Iterators.filter(iter, new UidPatternFilter(uidPattern));
            return ImmutableList.copyOf(iter);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Query");
            sb.append("{table='").append(table).append('\'');
            sb.append(", fromDate=").append(toLocalDateTime(fromDate));
            sb.append(", toDate=").append(toLocalDateTime(toDate));
            sb.append(", uid='").append(uid).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    private static class PartitionFullNameFun implements Function<Partition, String> {
        @Override
        public String apply(Partition input) {
            return input.toString();
        }
    }

    private static class FromDateFilter implements Predicate<Partition> {
        private final long date;

        private FromDateFilter(long date) {
            this.date = date;
        }

        @Override
        public boolean apply(Partition input) {
            return input.getTo() >= date;
        }
    }

    private static class ToDateFilter implements Predicate<Partition> {
        private final long date;

        private ToDateFilter(long date) {
            this.date = date;
        }

        @Override
        public boolean apply(Partition input) {
            return input.getFrom() <= date;
        }
    }

    private static class UidFilter implements Predicate<Partition> {
        private final String uid;

        private UidFilter(String uid) {
            this.uid = uid;
        }

        @Override
        public boolean apply(Partition input) {
            return input.getUid().equals(uid);
        }
    }

    private static class UidPatternFilter implements Predicate<Partition> {
        private Pattern uidPattern;

        private UidPatternFilter(Pattern uidPattern) {
            this.uidPattern = uidPattern;
        }

        @Override
        public boolean apply(Partition input) {
            return uidPattern.matcher(input.getUid()).matches();
        }
    }
}
