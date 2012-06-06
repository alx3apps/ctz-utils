package ru.concerteza.util.db.springjdbc.entitymapper.choosers;

import com.google.common.base.Function;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import ru.concerteza.util.db.springjdbc.entitymapper.SubclassChooser;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@link SubclassChooser} implementation that selects subclasses collating value of discriminator column defined in
 * {@link DiscriminatorColumn} annotation of given entity class and their {@link DiscriminatorValue} annotation values.
 *
 *
 * @author Timofey Gorshkov
 * Created 04.06.2012
 * @since  2.5.1
 * @see ru.concerteza.util.db.springjdbc.entitymapper.SubclassChooser
 * @see ru.concerteza.util.db.springjdbc.entitymapper.EntityMapper
 */
public class DiscriminatedSubclassChooser<T> implements SubclassChooser<T> {

    private String discColumn;
    private Map<String, Class<? extends T>> classMap;
    private Set<Class<? extends T>> subclasses;

    /**
     * @param entitySubclasses
     */
    public DiscriminatedSubclassChooser(Class<? extends T>... entitySubclasses) {

        DiscriminatorColumn dc;
        Class entityClass = entitySubclasses[0];
        while ((dc = (DiscriminatorColumn)entityClass.getAnnotation(DiscriminatorColumn.class)) == null) {
            entityClass = entityClass.getSuperclass();
            checkArgument(entityClass != Object.class, "Couldn't find discriminator column.");
        }
        for (int i = 1; i < entitySubclasses.length; i++) {
            checkArgument(entityClass.isAssignableFrom(entitySubclasses[i]), "Given entity subclasses don't have the same entity supercalss.");
        }

        discColumn = dc.name();
        subclasses = ImmutableSet.<Class<? extends T>>copyOf(entitySubclasses);
        classMap = Maps.uniqueIndex(subclasses, ClassDiscriminatorValue.FUNCTION);
    }

    /**
     * @return list of all subclasses, that will be used in row mapping
     */
    @Override
    public Set<Class<? extends T>> subclasses() {
        return subclasses;
    }

    /**
     * @param dataMap row data after applying all filters
     * @return class for concrete entity to instantiate for given row data
     */
    @Override
    public Class<? extends T> choose(Map<String, Object> dataMap) {
        String disc = (String) dataMap.get(discColumn);
        Class<? extends T> clazz = classMap.get(disc);
        checkArgument(clazz != null, "Couldn't find class for column '%s' from '%s' discriminator '%s' in '%s'.", discColumn, dataMap, disc, classMap);
        return clazz;
    }


    private enum ClassDiscriminatorColumn implements Function<Class,String> {
        FUNCTION;
        @Override
        public String apply(Class clazz) {
            DiscriminatorColumn discColumn = (DiscriminatorColumn)clazz.getAnnotation(DiscriminatorColumn.class);
            checkArgument(discColumn != null, "There is no DiscriminatorColumn annotation presented for class '%s'.", clazz);
            return discColumn.name();
        }
    }

    private enum ClassDiscriminatorValue implements Function<Class,String> {
        FUNCTION;
        @Override
        public String apply(Class clazz) {
            DiscriminatorValue discValue = (DiscriminatorValue)clazz.getAnnotation(DiscriminatorValue.class);
            checkArgument(discValue != null, "There is no DiscriminatorValue annotation presented for class '%s'.", clazz);
            return discValue.value();
        }
    }
}
