package ru.concerteza.util.db.springjdbc.querybuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
* User: alexkasko
* Date: 11/7/12
*/
class ExprList {
    private static final long serialVersionUID = 7616107916993782428L;
    private static final String DELIMITER = ", ";

    private final List<Expression> exprs = new ArrayList<Expression>();

    ExprList add(Expression expr) {
        this.exprs.add(expr);
        return this;
    }

    @Override
    public String toString() {
        return StringUtils.join(exprs, DELIMITER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ExprList that = (ExprList) o;
        return new EqualsBuilder()
                .append(exprs, that.exprs)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(exprs)
                .toHashCode();
    }
}
