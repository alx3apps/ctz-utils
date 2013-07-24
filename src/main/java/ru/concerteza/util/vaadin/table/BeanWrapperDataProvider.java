package ru.concerteza.util.vaadin.table;

import java.util.Collection;

/**
 * User: alexkasko
 * Date: 2/3/13
 */
public interface BeanWrapperDataProvider {
    Collection<?> loadPage(int limit, int offset);
}
