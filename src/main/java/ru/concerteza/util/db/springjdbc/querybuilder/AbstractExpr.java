package ru.concerteza.util.db.springjdbc.querybuilder;

/**
 * User: alexkasko
 * Date: 11/7/12
 */
abstract class AbstractExpr implements Expression {
    private static final long serialVersionUID = -8223267461679410411L;

    @Override
    public Expression and(Expression expr) {
        return new AndExpr(this, expr);
    }
}
