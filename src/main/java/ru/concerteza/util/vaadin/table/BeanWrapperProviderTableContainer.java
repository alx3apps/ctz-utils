package ru.concerteza.util.vaadin.table;

import com.vaadin.data.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: alexkasko
 * Date: 2/3/13
 */
public class BeanWrapperProviderTableContainer extends BeanWrapperTableContainer {
    private static final long serialVersionUID = -7066268711647310689L;
    private final BeanWrapperDataProvider provider;
    private final int pageSize;
    private final BeanWrapperItem[] page;
    private int startIndex = Integer.MIN_VALUE;

    public BeanWrapperProviderTableContainer(TableBindingMetadata metadata, int size, BeanWrapperDataProvider provider,
                                             int pageSize) {
        super(metadata, size);
        this.provider = provider;
        this.pageSize = pageSize;
        this.page = new BeanWrapperItem[pageSize];
    }

    @Override
    public Item getItem(Object itemId) {
        int index = (Integer) itemId;
        if (index >= startIndex && index < startIndex + pageSize) {
            return page[index - startIndex];
        }
        int skipPages = index / pageSize;
        startIndex = skipPages * pageSize;
        load();
        return page[index - startIndex];
    }

    // todo: dirty tails
    private void load() {
        Collection<?> data = provider.loadPage(pageSize, startIndex);
        int i = 0;
        for (Object ob : data) {
            page[i] = new BeanWrapperItem(ob);
            i += 1;
        }
    }
}
