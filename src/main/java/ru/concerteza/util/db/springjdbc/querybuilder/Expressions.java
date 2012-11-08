package ru.concerteza.util.db.springjdbc.querybuilder;

/**
 * User: alexkasko
 * Date: 11/7/12
 */
public final class Expressions {

    private Expressions() { }

    public static Expression literal(String str) {
        return new LiteralExpr(str);
    }

    public static Expression not(Expression expr) {
        return new NotExpr(expr);
    }

    public static Expression or(Expression left, Expression right) {
        return new OrExpr(left, right);
    }

    public static Expression select(String str) {
        return literal(str);
    }

    public static Expression where(String str) {
        return new LiteralExpr(str);
    }

    public static Expression groupBy(String str) {
        return new LiteralExpr(str);
    }

    public static Expression having(String str) {
        return new LiteralExpr(str);
    }

    public static Expression orderBy(String str) {
        return new LiteralExpr(str);
    }
}
