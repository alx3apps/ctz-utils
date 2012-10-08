package ru.concerteza.util.reflect;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;
import ru.concerteza.util.option.Option;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.reflect.CtzReflectionUtils.*;
import static ru.concerteza.util.reflect.CtzReflectionUtils.assign;

/**
 * User: alexey
 * Date: 4/2/12
 */
public class CtzReflectionUtilsTest {

    @Test
    public void testFields() {
        List<Field> fields = collectFields(TestParams.class);
        assertEquals("size fail", 2, fields.size());
        assertEquals("child fail", "bar", fields.get(0).getName());
        assertEquals("parent fail", "foo", fields.get(1).getName());
    }

    @Test
    public void testFieldsPredicate() {
        List<Field> fields = collectFields(TestParams.class, NotFooFieldPredicate.INSTANCE);
        assertEquals("size fail", 1, fields.size());
        assertEquals("child fail", "bar", fields.get(0).getName());
    }

    @Test
    public void testMethods() {
        List<Method> methods = collectMethods(TestParams.class, NotObjectMethodPredicate.INSTANCE);
        assertEquals("size fail", 2, methods.size());
        assertEquals("child fail", "bar", methods.get(0).getName());
        assertEquals("parent fail", "foo", methods.get(1).getName());
    }

    @Test
    public void testMethodsPredicate() {
        List<Method> methods = collectMethods(TestParams.class, NotFooMethodPredicate.INSTANCE);
        assertEquals("size fail", 1, methods.size());
        assertEquals("child fail", "bar", methods.get(0).getName());
    }

    @Test
    public void testInner() {
        assertTrue(CtzReflectionUtils.isInner(InnerOne.class));
        assertTrue(CtzReflectionUtils.isInner(PrivateInnerOne.class));
        assertFalse(CtzReflectionUtils.isInner(NotInnerOne.class));
        assertFalse(CtzReflectionUtils.isInner(PrivateNotInnerOne.class));
    }

    @Test
    public void testAssign() throws NoSuchFieldException {
        Foo foo = new Foo();
        Field fi = foo.getClass().getDeclaredField("bar");
        assign(foo, fi, "baz");
        assertEquals("baz", foo.bar);
    }

//    @Test
//    public void testAssignPrimitive() throws NoSuchFieldException {
//        PrimitiveFields obj = new PrimitiveFields();
//        Class<PrimitiveFields> clazz = PrimitiveFields.class;
//        assignPrimitiveOrString(obj, clazz.getDeclaredField("stringField"), "foo");
//        assertEquals("String fail", "foo", obj.stringField);
//        assignPrimitiveOrString(obj, clazz.getDeclaredField("booleanField"), "true");
//        assertEquals("Boolean fail", true, obj.booleanField);
//        assignPrimitiveOrString(obj, clazz.getDeclaredField("intField"), "424242");
//        assertEquals("Integer fail", 424242, obj.intField);
//        assignPrimitiveOrString(obj, clazz.getDeclaredField("byteField"), "42");
//        assertEquals("Byte fail", 42, obj.byteField);
//        assignPrimitiveOrString(obj, clazz.getDeclaredField("shortField"), "4242");
//        assertEquals("Short fail", 4242, obj.shortField);
//        assignPrimitiveOrString(obj, clazz.getDeclaredField("longField"), "2147483648");
//        assertEquals("Long fail", 2147483648L, obj.longField);
//        assignPrimitiveOrString(obj, clazz.getDeclaredField("floatField"), "42.42");
//        assertEquals("Float fail", 42.42, obj.floatField, 0.0001);
//        assignPrimitiveOrString(obj, clazz.getDeclaredField("doubleField"), "4242.4242");
//        assertEquals("Double fail", 4242.4242, obj.doubleField, 0.0001);
//        assignPrimitiveOrString(obj, clazz.getDeclaredField("charField"), "0");
//        assertEquals("Char fail", '0', obj.charField);
//    }

    @Test(expected = UnhandledException.class)
    public void testConstructorFail() {
        invokeDefaultConstructor(NoDefaultConstructor.class, this);
    }

    @Test
    public void testConstructor() {
        Enclosed enc = invokeDefaultConstructor(Enclosed.class, this);
        assertNotNull(enc);

        NonEnclosed notEnc = invokeDefaultConstructor(NonEnclosed.class, null);
        assertNotNull(notEnc);

        NonEnclosed notEnc1 = invokeDefaultConstructor(NonEnclosed.class, this);
        assertNotNull(notEnc1);
    }

    @Test
    public void testIsAssignableBoxed() {
        assertTrue(isAssignableBoxed(boolean.class, Boolean.class));
        assertTrue(isAssignableBoxed(Long.class, long.class));
        assertTrue(isAssignableBoxed(Integer.class, Integer.class));
        assertTrue(isAssignableBoxed(Byte.class, byte.class));
        assertFalse(isAssignableBoxed(String.class, Map.class));
    }

    @Test
    public void testMapToObject() throws NoSuchFieldException {
        Map<String, Field> columnMap = ImmutableMap.of("foo", Bar.class.getDeclaredField("foo"), "bar", Bar.class.getDeclaredField("bar"));
        {
            Bar bar = mapToObject(ImmutableMap.of("foo", "baz", "bar", 42L), Bar.class, columnMap);
            assertNotNull("Create fail", bar);
            assertEquals("Field fail", "baz", bar.foo);
            assertEquals("Field fail", 42L, bar.bar);
        }
        { // option case
            Bar barWithNull = mapToObject(ImmutableMap.of("foo", Option.none(), "bar", 42L), Bar.class, columnMap);
            assertNotNull("Create fail", barWithNull);
            assertEquals("Field fail", null, barWithNull.foo);
            assertEquals("Field fail", 42L, barWithNull.bar);
        }
    }

    @Test
    public void testFindGetter() {
        Method getter = findGetter(Getterable.class, "foo");
        assertNotNull("Create fail", getter);
        assertEquals("Return fail", "42", invokeMethod(new Getterable(), getter));
    }

    class InnerOne{}
    private class PrivateInnerOne{}
    static class NotInnerOne{}
    private static class PrivateNotInnerOne {}

    private class PrimitiveFields {
        private String stringField;
        private boolean booleanField;
        private int intField;
        private byte byteField;
        private short shortField;
        private long longField;
        private float floatField;
        private double doubleField;
        private char charField;
    }

    private class Enclosed {}
    private static class NonEnclosed {}
    private static class NoDefaultConstructor {
        private NoDefaultConstructor(String dummy) {}
    }

    private enum NotFooFieldPredicate implements Predicate<Field> {
        INSTANCE;
        @Override
        public boolean apply(Field input) {
            return !"foo".equals(input.getName());
        }
    }

    private enum NotObjectMethodPredicate implements Predicate<Method> {
        INSTANCE;
        @Override
        public boolean apply(Method input) {
            try {
                Object.class.getDeclaredMethod(input.getName(), input.getParameterTypes());
                return false;
            } catch(NoSuchMethodException e) {
                return true;
            }
        }
    }

    private enum NotFooMethodPredicate implements Predicate<Method> {
        INSTANCE;
        @Override
        public boolean apply(Method input) {
            return NotObjectMethodPredicate.INSTANCE.apply(input) && !"foo".equals(input.getName());
        }
    }

    private class Foo {
        private String bar;
    }

    private static class Bar {
        private String foo;
        private long bar;

        private Bar() {
        }

        private Bar(String foo, long bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }

    private static class Getterable {
        private final String foo = "42";

        public String getFoo() {
            return foo;
        }
    }
}

abstract class SomeParent {
    protected String foo;

    private String foo() {
        return foo;
    }
}

class TestParams extends SomeParent {
    private String bar;

    private String bar() {
        return bar;
    }
}
