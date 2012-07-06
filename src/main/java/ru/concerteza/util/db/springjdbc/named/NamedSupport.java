package ru.concerteza.util.db.springjdbc.named;

import com.google.common.collect.ImmutableList;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.List;

import static ru.concerteza.util.CtzFormatUtils.format;

/**
 * User: alexey
 * Date: 7/5/12
 */
class NamedSupport {
    static boolean isNamed(Annotation[][] anArray, String coStr) {
        int count = 0;
        for(Annotation[] anns : anArray) {
            for(Annotation an : anns) {
                if(Named.class.getName().equals(an.annotationType().getName())) {
                    count += 1;
                }
            }
        }
        if(0 == count) return false;
        if(anArray.length == count) return true;
        throw new IllegalArgumentException(format(
                "Not consistent @Named annotations found for constructor: '{}'", coStr));
    }

    static List<String> extractNames(Annotation[][] anArray) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for(Annotation[] anns : anArray) {
            for(Annotation an : anns) {
                if(Named.class.getName().equals(an.annotationType().getName())) {
                    Named na = (Named) an;
                    builder.add(na.value());
                }
            }
        }
        return builder.build();
    }
}
