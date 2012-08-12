package ru.concerteza.util.reflect;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.*;
import org.apache.commons.lang.UnhandledException;
import ru.concerteza.util.option.Option;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.WordUtils.capitalize;

/**
 * Various reflections utilities
 *
 * @author alexey
 * Date: 3/18/12
 * @see CtzReflectionUtilsTest
 */
public class CtzReflectionUtils {
    /**
     * Mapping of all primitive types to their corresponding boxed types
     */
    public static final BiMap<Class<?>, Class<?>> BOXING_MAP = ImmutableBiMap.<Class<?>, Class<?>>builder()
            .put(Boolean.TYPE, Boolean.class)
            .put(Integer.TYPE, Integer.class)
            .put(Byte.TYPE, Byte.class)
            .put(Short.TYPE, Short.class)
            .put(Long.TYPE, Long.class)
            .put(Float.TYPE, Float.class)
            .put(Double.TYPE, Double.class)
            .put(Character.TYPE, Character.class)
            .build();

    /**
     * Shortcut method for collecting all declared fields
     *
     * @param clazz class to collect fields from
     * @return list of all class and its superclasses fields
     */
    public static List<Field> collectFields(Class<?> clazz) {
        return collectFields(clazz, Predicates.<Field>alwaysTrue());
    }

    /**
     * Collects all declared fields of class and all its superclasses
     *
     * @param clazz class to collect fields from
     * @param predicate field predicate
     * @return list of all class and its superclasses fields
     */
    public static List<Field> collectFields(Class<?> clazz, Predicate<Field> predicate) {
        ImmutableList.Builder<Field> builder = ImmutableList.builder();
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

    /**
     * Collects all declared methods of class and all its superclasses
     *
     * @param clazz class to collect methods from
     * @param predicate method predicate
     * @return list of all class and its superclasses methods
     */
    public static List<Method> collectMethods(Class<?> clazz, Predicate<Method> predicate) {
        ImmutableList.Builder<Method> builder = ImmutableList.builder();
        collectMethodsRecursive(builder, clazz, predicate);
        return builder.build();
    }

    private static void collectMethodsRecursive(ImmutableList.Builder<Method> results, Class<?> clazz, Predicate<Method> predicate) {
        Method[] methods = clazz.getDeclaredMethods();
        // own methods, methods is not iterable so prevent intermediate list
        for(Method me : methods) {
            if(predicate.apply(me)) results.add(me);
        }
        // parent methods
        if (null != clazz.getSuperclass()) {
            collectMethodsRecursive(results, clazz.getSuperclass(), predicate);
        }
    }

    /**
     * Checks whether class is inner (non static) one
     *
     * @param clazz class to check
     * @return inner or not
     */
    public static boolean isInner(Class<?> clazz) {
        if(null == clazz.getEnclosingClass()) return false;
        return (clazz.getModifiers() & Modifier.STATIC) == 0;
    }

    /**
     * Assignes value to field wrapping IAE into runtime exception
     * and making field accessible
     *
     * @param obj object containing field
     * @param fi field
     * @param value value to assign
     */
    public static void assign(Object obj, Field fi, Object value) {
        try {
            if (!fi.isAccessible()) fi.setAccessible(true);
            fi.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new UnhandledException(e);
        }
    }

//    public static void assignPrimitiveOrString(Object obj, Field field, String valueString) {
//        try {
//            final Object value;
//            if (!field.getType().isPrimitive()) {
//                if (!String.class.isAssignableFrom(field.getType())) throw new IllegalArgumentException(format(
//                        "Only String and primitive fields of input objects are supported, found: '{}'", field.getType()));
//                value = valueString;
//            } else if (Boolean.TYPE.equals(field.getType())) value = Boolean.parseBoolean(valueString);
//            else if (Integer.TYPE.equals(field.getType())) value = Integer.parseInt(valueString);
//            else if (Byte.TYPE.equals(field.getType())) value = Byte.parseByte(valueString);
//            else if (Short.TYPE.equals(field.getType())) value = Short.parseShort(valueString);
//            else if (Long.TYPE.equals(field.getType())) value = Long.parseLong(valueString);
//            else if (Float.TYPE.equals(field.getType())) value = Float.parseFloat(valueString);
//            else if (Double.TYPE.equals(field.getType())) value = Double.parseDouble(valueString);
//            else if (Character.TYPE.equals(field.getType())) {
//                if (valueString.length() != 1) throw new IllegalArgumentException(format(
//                        "Parameter: '{}' for field: '{}' doesn't suit to char field", valueString, field.getName()));
//                value = valueString.charAt(0);
//            }
//            cannot happen
//            else throw new IllegalStateException(format("Unknown primitive field type: '{}'", field.getType()));
//
//            field.setAccessible(true);
//            field.set(obj, value);
//        } catch (IllegalAccessException e) {
//            throw new UnhandledException(e);
//        }
//    }

    /**
     * Invoking methods wrapping IAE and ITE into runtime exception
     * and making method accessible if necessary
     *
     * @param obj object containing method
     * @param method method to invoke
     * @param args method arguments
     * @param <T> method return type
     * @return method invocation result
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object obj, Method method, Object... args) {
        try {
            if(!method.isAccessible()) method.setAccessible(true);
            return (T) method.invoke(obj, args);
        } catch(InvocationTargetException e) {
            throw new UnhandledException(e);
        } catch(IllegalAccessException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Invokes provided constructor wrapping checked exceptions into runtime ones
     * and making it accessible if necessary
     *
     * @param constr constructor to invoke
     * @param args constructor arguments
     * @param <T> instantiated class type
     * @return instantiated object
     */
    public static <T> T invokeConstructor(Constructor<T> constr, Object... args) {
        try {
            if(!constr.isAccessible()) constr.setAccessible(true);
            return constr.newInstance(args);
        } catch(InstantiationException e) {
            throw new UnhandledException(e);
        } catch(IllegalAccessException e) {
            throw new UnhandledException(e);
        } catch(InvocationTargetException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Default constructor invocation shortcut method
     *
     * @param clazz class to instantiate
     * @param <T> class type
     * @return instantiated class
     */
    public static <T> T invokeDefaultConstructor(Class<T> clazz) {
        return invokeDefaultConstructor(clazz, null);
    }

    /**
     * Invokes constructor wrapping exceptions into runtime exception and supporting inner class instantiation
     * with provided outer instance
     *
     * @param clazz class to instantiate
     * @param enclosingInstance enclosing object instance (for inner classes)
     * @param <T> class type
     * @return instantiated class
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeDefaultConstructor(Class<T> clazz, Object enclosingInstance) {
        try {
            final T res;
            if (CtzReflectionUtils.isInner(clazz)) {
                checkNotNull(enclosingInstance, "Enclosing instance must be provided for inner class");
                // http://stackoverflow.com/questions/4407726/dynamically-instantiating-an-inner-class-nested-inside-an-abstract-class/4407775#4407775
                // todo maybe add support for children (proxies) enclosing classes using isAssignableFrom
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

    /**
     * Converts map into object instance using default constructor and field access
     *
     * @param dataMap data map
     * @param clazz class to instantiate
     * @param fieldMap field map, must correspond to data map
     * @param <T> class type
     * @return instantiates class with fieds assigned
     */
    public static <T> T mapToObject(Map<String, ?> dataMap, Class<T> clazz, Map<String, Field> fieldMap) {
        T res = invokeDefaultConstructor(clazz);
        for (Map.Entry<String, Field> en : fieldMap.entrySet()) {
            final Object colVal = dataMap.get(en.getKey());
            checkArgument(null != colVal, "Cannot find input value for column: '%s', class: '%s', fieldMap keys: '%s', dataMap: '%s'", en.getKey(), clazz.getName(), fieldMap.keySet(), dataMap);
            final Object val;
            if(Option.class.isAssignableFrom(colVal.getClass())) {
                Option<?> opt = (Option<?>) colVal;
                val = opt.getIfAny(null);
            } else val = colVal;
            if(null != val) checkArgument(isAssignableBoxed(en.getValue().getType(), val.getClass()), "Cannot map column: '%s', source type: '%s', target type: '%s'", en.getKey(), val.getClass(), en.getValue().getType());
            assign(res, en.getValue(), val);
        }
        return res;
    }

    /**
     * Check whether one type is assignable from other taking into consideration autoboxing
     *
     * @param cl1 first type
     * @param cl2 second type
     * @return true if first type (maybe autoboxed) is assignable from second class (maybe autoboxed)
     */
    public static boolean isAssignableBoxed(Class<?> cl1, Class<?> cl2) {
        if(cl1.isAssignableFrom(cl2)) return true;
        if(cl1.isPrimitive() && cl2.isPrimitive()) return false;
        if(cl1.isPrimitive()) return BOXING_MAP.get(cl1).isAssignableFrom(cl2);
        if(cl2.isPrimitive()) return cl1.isAssignableFrom(BOXING_MAP.get(cl2));
        return false;
    }
}
