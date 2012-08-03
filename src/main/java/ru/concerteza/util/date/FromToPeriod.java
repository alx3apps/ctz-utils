package ru.concerteza.util.date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Utility class for holding from-to date pairs
 *
 * @author alexey
 * Date: 7/29/12
 */
public class FromToPeriod {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * @param from start date
     * @param to end date
     */
    public FromToPeriod(LocalDateTime from, LocalDateTime to) {
        checkArgument(!from.isAfter(to));
        this.from = from;
        this.to = to;
    }

    /**
     * @return start date
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * @return end date
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("from", from).
                append("to", to).
                toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        FromToPeriod that = (FromToPeriod) o;
        return new EqualsBuilder()
                .append(from, that.from)
                .append(to, that.to)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(from)
                .append(to)
                .toHashCode();
    }
}
