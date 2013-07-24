package ru.concerteza.util.vaadin.table;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: alexkasko
 * Date: 2/3/13
 */
public abstract class BeanWrapperTableContainer extends AbstractContainer {
    private static final long serialVersionUID = 828707812882269031L;
    private final TableBindingMetadata metadata;
    private final int size;

    protected BeanWrapperTableContainer(TableBindingMetadata metadata, int size) {
        this.metadata = metadata;
        this.size = size;
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return metadata.getPropertyIds();
    }

    @Override
    public Collection<?> getItemIds() {
        ArrayList<Integer> res = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) res.add(i);
        return res;
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        return getItem(itemId).getItemProperty(propertyId);
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return metadata.getType(propertyId);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean containsId(Object itemId) {
        int id = (Integer) itemId;
        return id > 0 && id < size;
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("addItem");
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("addItem");
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("removeItem");
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("addContainerProperty");
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("removeContainerProperty");
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("removeAllItems");
    }
}
