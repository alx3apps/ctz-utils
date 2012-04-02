package ru.concerteza.util;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    class InnerOne{}
    private class PrivateInnerOne{}
    static class NotInnerOne{}
    private static class ProvateNotInnerOne{}
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
