package ru.concerteza.util.db.partition;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import static ru.concerteza.util.date.CtzDateUtils.toLocalDateTime;

/**
 * User: alexkasko
 * Date: 11/10/14
 */
public class Partition implements Serializable {
    private static final long serialVersionUID = -7220045800403400036L;

    private final DateTimeFormatter fromFormat;
    private final DateTimeFormatter toFormat;
    private final String name;
    private final long from;
    private final long to;
    private final String postfix;
    private final String fullName;
    private final String uid;

    Partition(DateTimeFormatter fromFormat, DateTimeFormatter toFormat, String name, long from, long to, String uid) {
        this.fromFormat = fromFormat;
        this.toFormat = toFormat;
        this.name = name;
        this.from = from;
        this.to = to;
        this.uid = uid;
        this.postfix = this.fromFormat.format(toLocalDateTime(from))
                + "_" + this.toFormat.format(toLocalDateTime(to)) + "_" + uid;
        this.fullName = name + "_" + postfix;
    }

    public String getName() {
        return name;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public String getPostfix() {
        return postfix;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
