package ru.concerteza.util;

import org.apache.commons.lang.UnhandledException;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;
import static ru.concerteza.util.CtzReflectionUtils.assignPrimitiveOrString;
import static ru.concerteza.util.CtzReflectionUtils.callDefaultConstructor;

/**
 * User: alexey
 * Date: 4/2/12
 */
public class CtzReflectionUtilsTest {

    @Test
    public void testFields() {
        List<Field> fields = CtzReflectionUtils.allFields(TestParams.class);
        Assert.assertEquals("size fail", 2, fields.size());
        Assert.assertEquals("child fail", "bar", fields.get(0).getName());
        Assert.assertEquals("parent fail", "foo", fields.get(1).getName());
    }

    @Test
    public void testInner() {
        assertTrue(CtzReflectionUtils.isInner(InnerOne.class));
        assertTrue(CtzReflectionUtils.isInner(PrivateInnerOne.class));
        assertFalse(CtzReflectionUtils.isInner(NotInnerOne.class));
        assertFalse(CtzReflectionUtils.isInner(ProvateNotInnerOne.class));
    }

    @Test
    public void testAssign() throws NoSuchFieldException {
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

    class InnerOne{}
    private class PrivateInnerOne{}
    static class NotInnerOne{}
    private static class ProvateNotInnerOne{}

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
