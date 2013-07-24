package ru.concerteza.util.vaadin.table;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: alexkasko
 * Date: 2/3/13
 */
class BeanWrapperItem implements Item {
    private static final long serialVersionUID = 6247259257857254348L;
    private final BeanWrapper bean;

    BeanWrapperItem(Object bean) {
        this.bean = new BeanWrapperImpl(bean);
    }

    @Override
    public Property getItemProperty(Object id) {
        String propertyId = id.toString();
        Object value = bean.getPropertyValue(propertyId);
        Class<?> type = bean.getPropertyType(propertyId);
        return new BeanWrapperProperty(value, type);
    }

    @Override
    public Collection<?> getItemPropertyIds() {
        PropertyDescriptor[] pds = bean.getPropertyDescriptors();
        List<Object> res = new ArrayList<Object>(pds.length - 1);
        for(PropertyDescriptor pd : pds) {
            String name = pd.getName();
            if(!"class".equals(name)) res.add(name);
        }
        return res;
    }

    @Override
    public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("addItemProperty");
    }

    @Override
    public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("removeItemProperty");
    }
}
