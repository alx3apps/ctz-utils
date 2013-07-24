package ru.concerteza.util.vaadin.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: alexkasko
 * Date: 2/3/13
 */
public class TableBindingMetadata {
    private final List<ColumnBindingMetadata> columns;
    private final List<String> propertyIds;
    private final Map<String, Class<?>> typesMap;

    public TableBindingMetadata(List<ColumnBindingMetadata> columns) {
        if (null == columns) throw new IllegalArgumentException("Provided columns arg is null");
        if (0 == columns.size()) throw new IllegalArgumentException("Provided columns are empty");
        this.columns = columns;
        this.propertyIds = new ArrayList<String>(columns.size());
        this.typesMap = new HashMap<String, Class<?>>(columns.size());
        for (ColumnBindingMetadata co : columns) {
            this.propertyIds.add(co.getPropertyId());
            this.typesMap.put(co.getPropertyId(), co.getPropertyType());
        }
    }

    public List<ColumnBindingMetadata> getColumns() {
        return columns;
    }

    public List<String> getPropertyIds() {
        return propertyIds;
    }

    public Class<?> getType(Object propertyId) {
        return typesMap.get(propertyId.toString());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TableBindingMetadata");
        sb.append("{columns=").append(columns);
        sb.append('}');
        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<ColumnBindingMetadata> builder = new ArrayList<ColumnBindingMetadata>();

        public Builder add(String name, String description, Class<?> type) {
            builder.add(new ColumnBindingMetadata(name, description, type));
            return this;
        }

        public TableBindingMetadata build() {
            return new TableBindingMetadata(builder);
        }
    }
}
