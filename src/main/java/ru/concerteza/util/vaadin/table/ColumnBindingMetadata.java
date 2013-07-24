package ru.concerteza.util.vaadin.table;

/**
 * User: alexkasko
 * Date: 2/3/13
 */
public class ColumnBindingMetadata {
    private final String propertyId;
    private final String caption;
    private final Class<?> propertyType;

    public ColumnBindingMetadata(String propertyId, String caption, Class<?> propertyType) {
        this.propertyId = propertyId;
        this.caption = caption;
        this.propertyType = propertyType;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getCaption() {
        return caption;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ColumnBindingMetadata");
        sb.append("{propertyId=").append(propertyId);
        sb.append(", caption='").append(caption).append('\'');
        sb.append(", propertyType=").append(propertyType);
        sb.append('}');
        return sb.toString();
    }
}
