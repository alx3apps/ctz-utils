package ru.concerteza.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.UnhandledException;

import javax.annotation.Nullable;
import javax.persistence.Column;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static ru.concerteza.util.CtzFormatUtils.format;

/**
* User: alexey
* Date: 3/18/12
*/
public class CtzReflectionUtils {
    private static final AnnotatedColumnPredicate ANNOTATED_COLUMN_PREDICATE = new AnnotatedColumnPredicate();
    private static final Map<Class<?>, Class<?>> BOXING_MAP = ImmutableMap.<Class<?>, Class<?>>builder()
            .put(Boolean.TYPE, Boolean.class)
            .put(Integer.TYPE, Integer.class)
            .put(Byte.TYPE, Byte.class)
            .put(Short.TYPE, Short.class)
            .put(Long.TYPE, Long.class)
            .put(Float.TYPE, Float.class)
            .put(Double.TYPE, Double.class)
            .put(Character.TYPE, Character.class)
            .build();

    public static List<Field> collectFields(Class<?> clazz) {
        return collectFields(clazz, Predicates.<Field>alwaysTrue());
    }

    public static List<Field> collectFields(Class<?> clazz, Predicate<Field> predicate) {
        ImmutableList.Builder<Field> builder = new ImmutableList.Builder<Field>();
        collectFieldsRecursive(builder, clazz, predicate);
        return builder.build();
    }

    // http://stackoverflow.com/questions/1042798/retrieving-the-inherited-attribute-names-values-using-java-reflection/1042827#1042827
    private static void collectFieldsRecursive(ImmutableList.Builder<Field> results, Class<?> clazz, Predicate<Field> predicate) {
        Field[] fields = clazz.getDeclaredFields();
        // own fields, fields is not iterable so prevent intermediate list
        for(Field fi : fields) {
            if(predicate.apply(fi)) results.add(fi);
        }
        // parent fields
        if (null != clazz.getSuperclass()) {
            collectFieldsRecursive(results, clazz.getSuperclass(), predicate);
        }
    }

    public static boolean isInner(Class<?> clazz) {
        if(null == clazz.getEnclosingClass()) return false;
        return (clazz.getModifiers() & Modifier.STATIC) == 0;
    }

    public static void assignPrimitiveOrString(Object obj, Field field, String valueString) {
        try {
            final Object value;
            if (!field.getType().isPrimitive()) {
                if (!String.class.isAssignableFrom(field.getType())) throw new IllegalArgumentException(format(
                        "Only String and primitive fields of input objects are supported, found: '{}'", field.getType()));
                value = valueString;
            } else if (Boolean.TYPE.equals(field.getType())) value = Boolean.parseBoolean(valueString);
            else if (Integer.TYPE.equals(field.getType())) value = Integer.parseInt(valueString);
            else if (Byte.TYPE.equals(field.getType())) value = Byte.parseByte(valueString);
            else if (Short.TYPE.equals(field.getType())) value = Short.parseShort(valueString);
            else if (Long.TYPE.equals(field.getType())) value = Long.parseLong(valueString);
            else if (Float.TYPE.equals(field.getType())) value = Float.parseFloat(valueString);
            else if (Double.TYPE.equals(field.getType())) value = Double.parseDouble(valueString);
            else if (Character.TYPE.equals(field.getType())) {
                if (valueString.length() != 1) throw new IllegalArgumentException(format(
                        "Parameter: '{}' for field: '{}' doesn't suit to char field", valueString, field.getName()));
                value = valueString.charAt(0);
            }
            // cannot happen
            else throw new IllegalStateException(format("Unknown primitive field type: '{}'", field.getType()));

            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new UnhandledException(e);
        }
    }

    public static <T> T callDefaultConstructor(Class<T> clazz) {
        return callDefaultConstructor(clazz, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T callDefaultConstructor(Class<T> clazz, Object enclosingInstance) {
        try {
            final T res;
            if (CtzReflectionUtils.isInner(clazz)) {
                checkNotNull(enclosingInstance, "Enclosing instance must be provided for inner class");
                // http://stackoverflow.com/questions/4407726/dynamically-instantiating-an-inner-class-nested-inside-an-abstract-class/4407775#4407775
                // todo maybe add support for children (prioxies) enclosing classes using isAssignableFrom
                Constructor con = clazz.getDeclaredConstructor(enclosingInstance.getClass());
                con.setAccessible(true);
                res = (T) con.newInstance(enclosingInstance);
            } else {
                Constructor con = clazz.getDeclaredConstructor();
                con.setAccessible(true);
                res = (T) con.newInstance();
            }
            return res;
        } catch (InstantiationException e) {
            throw new UnhandledException(e);
        } catch (IllegalAccessException e) {
            throw new UnhandledException(e);
        } catch (InvocationTargetException e) {
            throw new UnhandledException(e);
        } catch (NoSuchMethodException e) {
            throw new UnhandledException(e);
        }
    }

    public static Map<String, Field> columnsFieldMap(Class<?> clazz) {
        ImmutableMap.Builder<String, Field> builder = new ImmutableMap.Builder<String, Field>();
        for (Field fi : collectFields(clazz, ANNOTATED_COLUMN_PREDICATE)) {
            Column col = fi.getAnnotation(Column.class);
            String name = isNotEmpty(col.name()) ? col.name() : fi.getName();
            builder.put(name.toLowerCase(), fi);
        }
        return builder.build();
    }

    private static class AnnotatedColumnPredicate implements Predicate<Field> {
        @Override
        public boolean apply(Field input) {
            return null != input.getAnnotation(Column.class);
        }
    }

    public static boolean isAssignableBoxed(Class<?> cl1, Class<?> cl2) {
        if(cl1.isAssignableFrom(cl2)) return true;
        if(cl1.isPrimitive() && cl2.isPrimitive()) return false;
        if(cl1.isPrimitive()) return BOXING_MAP.get(cl1).isAssignableFrom(cl2);
        if(cl2.isPrimitive()) return cl1.isAssignableFrom(BOXING_MAP.get(cl2));
        return false;
    }
}
