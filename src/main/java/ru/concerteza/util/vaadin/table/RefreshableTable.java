package ru.concerteza.util.vaadin.table;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;

/**
 * User: alexkasko
 * Date: 7/24/13
 */
public class RefreshableTable {
    private final Table table;
    private final RefreshableTableDataBuilder data;
    private final int pageLength;

    public RefreshableTable(Table table, RefreshableTableDataBuilder data) {
        this(table, data, 15);
    }

    public RefreshableTable(Table table, RefreshableTableDataBuilder data, int pageLength) {
        this.table = table;
        this.data = data;
        this.pageLength = pageLength;
    }

    public void refresh() {
        Container container = data.loadData();
        table.setContainerDataSource(container);
        table.setPageLength(Math.min(pageLength, container.size()));
    }
}
