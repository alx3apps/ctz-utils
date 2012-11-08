package ru.concerteza.util.db.springjdbc.querybuilder;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * User: alexkasko
 * Date: 11/7/12
 */
class OrExpr extends AbstractExpr {
    private static final long serialVersionUID = -944146038841042727L;

    private final Expression left;
    private final Expression right;

    OrExpr(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + ") or (" + right + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        OrExpr that = (OrExpr) o;
        return new EqualsBuilder()
                .append(left, that.left)
                .append(right, that.right)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(left)
                .append(right)
                .toHashCode();
    }
}
