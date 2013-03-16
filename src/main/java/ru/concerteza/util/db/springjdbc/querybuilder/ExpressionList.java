package ru.concerteza.util.db.springjdbc.querybuilder;

/**
 * Interface for expression lists. List is joined from expressions with commas.
 * List is printed to output using {@link #toString()} method.
 *
 * @author alexkasko
 * Date: 11/8/12
 */
@Deprecated //use com.alexkasko.springjdbc:query-string-builder
public interface ExpressionList {
    /**
     * Add expression to list
     *
     * @param expr expression
     * @return list itself
     */
    ExpressionList comma(Expression expr);

    /**
     * Add expression to list
     *
     * @param expr expression literal
     * @return list itself
     */
    ExpressionList comma(String expr);
}
