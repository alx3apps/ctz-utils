package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.base.Function;

import java.util.Map;

/**
 * User: alexey
 * Date: 5/16/12
 */
public interface Filter extends Function<Map<String, Object>, Map<String, Object>> {
}
