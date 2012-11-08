package ru.concerteza.util.db.springjdbc.querybuilder;

import java.io.Serializable;

/**
 * User: alexkasko
 * Date: 11/7/12
 */
public interface Expression extends Serializable {
    Expression and(Expression expr);
}
