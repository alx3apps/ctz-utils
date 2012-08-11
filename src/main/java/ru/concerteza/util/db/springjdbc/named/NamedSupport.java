package ru.concerteza.util.db.springjdbc.named;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.LinkedHashSet;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * User: alexey
 * Date: 8/9/12
 */
class NamedSupport {
    @SuppressWarnings("unchecked")
    static <T> Optional<NamedConstructor<T>> extractNamedArgsConstructor(Class<T> clazz, boolean caseSensitiveNames) {
        NamedConstructor res = null;
        for(Constructor<?> co : clazz.getDeclaredConstructors()) {
            LinkedHashSet<NCArg> args = extractNames(co, caseSensitiveNames);
            if(args.size() > 0) {
                checkArgument(null == res, "Only one named constructor per class is allowed");
                res = new NamedConstructor(co, args);
            }
        }
        return (Optional) Optional.fromNullable(res);
    }

    private static LinkedHashSet<NCArg> extractNames(Constructor<?> co, boolean caseSensitiveNames) {
        LinkedHashSet<NCArg> res = Sets.newLinkedHashSet();
        String coStr = co.toGenericString();
        Annotation[][] anArray = co.getParameterAnnotations();
        Class<?>[] typesArray = co.getParameterTypes();
        for(int i = 0; i < anArray.length; i++) {
            Annotation[] anns = anArray[i];
            Class<?> type = typesArray[i];
            for(Annotation an : anns) {
                if(Named.class.getName().equals(an.annotationType().getName())) {
                    Named na = (Named) an;
                    checkArgument(isNotBlank(na.value()), "@Named annotation with empty value found, constructor: '%s'", coStr);
                    String name = caseSensitiveNames ? na.value() : na.value().toLowerCase(Locale.ENGLISH);
                    boolean unique = res.add(new NCArg(name, type, Optional.class.isAssignableFrom(type)));
                    checkArgument(unique, "Not unique @Named or @NamedList value: '%s', constructor: '%s'", na.value(), coStr);
                } else if(NamedGenericRef.class.getName().equals(an.annotationType().getName())) {
                    NamedGenericRef li = (NamedGenericRef) an;
                    checkArgument(isNotBlank(li.name()), "@NamedList annotation with empty value found, constructor: '%s'", coStr);
                    checkArgument(null != li.type(), "@NamedList annotation with null type found, constructor: '%s'", coStr);
                    String name = caseSensitiveNames ? li.name() : li.name().toLowerCase(Locale.ENGLISH);
                    boolean unique = res.add(new NCArg(name, li.type(), Optional.class.isAssignableFrom(type)));
                    checkArgument(unique, "Not unique @Named or @NamedList value: '%s', constructor: '%s'", li.name(), coStr);
                }
            }
        }
        checkArgument(0 == res.size() || anArray.length == res.size(), "Not consistent @Named annotations found for constructor: '%s'", coStr);
        return res;
    }
}
