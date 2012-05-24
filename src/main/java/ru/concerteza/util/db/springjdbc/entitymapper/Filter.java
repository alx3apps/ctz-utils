package ru.concerteza.util.db.springjdbc.entitymapper;

import com.google.common.base.Function;

import java.util.Map;

/**
 * Interfaces for filters, that will be applied to row data before mapping
 *
 * @author alexey
 * Date: 5/16/12
 * @see EntityMapper
 */
public interface Filter extends Function<Map<String, Object>, Map<String, Object>> {
}
