package ru.concerteza.util.db.springjdbc.querybuilder;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * User: alexkasko
 * Date: 11/7/12
 */
class LiteralExpr extends AbstractExpr {
    private static final long serialVersionUID = 6793424216407288186L;

    private final String literal;

    LiteralExpr(String literal) {
        this.literal = literal;
    }

    @Override
    public String toString() {
        return literal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        LiteralExpr that = (LiteralExpr) o;
        return new EqualsBuilder()
                .append(literal, that.literal)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(literal)
                .toHashCode();
    }
}
