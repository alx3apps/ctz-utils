package ru.concerteza.util;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.UnhandledException;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static ru.concerteza.util.CtzReflectionUtils.*;
import static ru.concerteza.util.CtzReflectionUtils.assign;

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
        List<Field> fields = collectFields(TestParams.class, new NotFooPredicate());
        assertEquals("size fail", 1, fields.size());
        assertEquals("child fail", "bar", fields.get(0).getName());
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

    @Test
    public void testAssignPrimitive() throws NoSuchFieldException {
        PrimitiveFields obj = new PrimitiveFields();
        Class<PrimitiveFields> clazz = PrimitiveFields.class;
        assignPrimitiveOrString(obj, clazz.getDeclaredField("stringField"), "foo");
        assertEquals("String fail", "foo", obj.stringField);
        assignPrimitiveOrString(obj, clazz.getDeclaredField("booleanField"), "true");
        assertEquals("Boolean fail", true, obj.booleanField);
        assignPrimitiveOrString(obj, clazz.getDeclaredField("intField"), "424242");
        assertEquals("Integer fail", 424242, obj.intField);
        assignPrimitiveOrString(obj, clazz.getDeclaredField("byteField"), "42");
        assertEquals("Byte fail", 42, obj.byteField);
        assignPrimitiveOrString(obj, clazz.getDeclaredField("shortField"), "4242");
        assertEquals("Short fail", 4242, obj.shortField);
        assignPrimitiveOrString(obj, clazz.getDeclaredField("longField"), "2147483648");
        assertEquals("Long fail", 2147483648L, obj.longField);
        assignPrimitiveOrString(obj, clazz.getDeclaredField("floatField"), "42.42");
        assertEquals("Float fail", 42.42, obj.floatField, 0.0001);
        assignPrimitiveOrString(obj, clazz.getDeclaredField("doubleField"), "4242.4242");
        assertEquals("Double fail", 4242.4242, obj.doubleField, 0.0001);
        assignPrimitiveOrString(obj, clazz.getDeclaredField("charField"), "0");
        assertEquals("Char fail", '0', obj.charField);
    }

    @Test(expected = UnhandledException.class)
    public void testConstructorFail() {
        callDefaultConstructor(NoDefaultConstructor.class, this);
    }

    @Test
    public void testConstructor() {
        Enclosed enc = callDefaultConstructor(Enclosed.class, this);
        assertNotNull(enc);

        NonEnclosed notEnc = callDefaultConstructor(NonEnclosed.class, null);
        assertNotNull(notEnc);

        NonEnclosed notEnc1 = callDefaultConstructor(NonEnclosed.class, this);
        assertNotNull(notEnc1);
    }

    @Test
    public void testColumnsFieldsMap() {
        Map<String, Field> map = columnFieldMap(Entity.class);
        assertEquals("Size fail", 2, map.size());
        assertTrue("Unnamed fail", map.containsKey("foo"));
        assertEquals("Unnamed wrong field fail", "foo", map.get("foo").getName());
        assertTrue("Named fail", map.containsKey("dummy"));
        assertEquals("Named wrong field fail", "bar", map.get("dummy").getName());
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
        Bar bar = mapToObject(ImmutableMap.of("foo", "baz", "bar", 42L), Bar.class, columnMap);
        assertNotNull("Create fail", bar);
        assertEquals("Field fail", "baz", bar.foo);
        assertEquals("Field fail", 42L, bar.bar);
    }

    @Test
    public void testObjectToMap() throws NoSuchFieldException {
        Bar bar = new Bar("baz", 42L);
        Map<String, Field> columnMap = ImmutableMap.of("foo", Bar.class.getDeclaredField("foo"), "bar", Bar.class.getDeclaredField("bar"));
        Map<String, Object> map = objectToMap(bar, columnMap);
        assertNotNull("Create fail", map);
        assertEquals("Size fail", 2, map.size());
        assertEquals("Field fail", "baz", map.get("foo"));
        assertEquals("Field fail", 42L, map.get("bar"));
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

    private class NotFooPredicate implements Predicate<Field> {
        @Override
        public boolean apply(Field input) {
            return !"foo".equals(input.getName());
        }
    }

    private class Entity {
        @Id
        @Column
        private long foo;
        @Column(name = "dummy")
        private String bar;
        private String throwaway;
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
}

abstract class SomeParent {
    protected String foo;

    public String getFoo() {
        return foo;
    }
}

class TestParams extends SomeParent {
    private String bar;

    public String getBar() {
        return bar;
    }
}
