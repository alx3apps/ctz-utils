package ru.concerteza.util.vaadin.table;

import com.vaadin.data.Property;

/**
 * User: alexkasko
 * Date: 2/3/13
 */
class BeanWrapperProperty implements Property {
    private static final long serialVersionUID = 2005889995725561099L;
    private final Object value;
    private final Class<?> type;

    BeanWrapperProperty(Object value, Class<?> type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
        throw new UnsupportedOperationException("setValue");
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void setReadOnly(boolean newStatus) {
        if(!newStatus) throw new IllegalArgumentException("This property is read-only");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
