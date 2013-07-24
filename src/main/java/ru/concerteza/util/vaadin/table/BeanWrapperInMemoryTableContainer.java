package ru.concerteza.util.vaadin.table;

import com.vaadin.data.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: alexkasko
 * Date: 2/3/13
 */
public class BeanWrapperInMemoryTableContainer extends BeanWrapperTableContainer {
    private static final long serialVersionUID = 4439832074973891456L;
    private final List<BeanWrapperItem> items;

    public BeanWrapperInMemoryTableContainer(TableBindingMetadata metadata, Collection<?> items) {
        super(metadata, items.size());
        this.items = new ArrayList<BeanWrapperItem>(items.size());
        for(Object ob : items) {
            this.items.add(new BeanWrapperItem(ob));
        }
    }

    @Override
    public Item getItem(Object itemId) {
        int index = (Integer) itemId;
        return items.get(index);
    }
}
