package ru.concerteza.util.option;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: alexey
 * Date: 5/8/12
 */

public class OptionTest {
    @Test
    public void testGet() {
        Object foo = new Object();
        assertEquals(foo, Option.some(foo).get());
    }

    @Test
    public void testGetIfAny() {
        Object foo = new Object();
        assertEquals(foo, Option.none().getIfAny(foo));
    }

    @Test
    public void testIsNone() {
        assertTrue(Option.none().isNone());
        assertFalse(Option.some(new Object()).isNone());
    }

    @Test
    public void testIsSome() {
        assertTrue(Option.some(new Object()).isSome());
        assertFalse(Option.none().isSome());
    }

    @Test
    public void testWrapNull() {
        assertTrue(Option.wrapNull(null).isNone());
        assertTrue(Option.wrapNull(new Object()).isSome());
    }

    @Test
    public void testWrapEmpty() {
        assertTrue(Option.wrapEmpty(null).isNone());
        assertTrue(Option.wrapEmpty("").isNone());
        assertTrue(Option.wrapEmpty("foo").isSome());
    }

    @Test
    public void testEquals() {
        Object foo = new Object();
        Object bar = new Object();
        assertTrue(Option.some(foo).equals(Option.some(foo)));
        assertFalse(Option.some(foo).equals(Option.some(bar)));
        assertFalse(Option.some(foo).equals(Option.none()));
        assertFalse(Option.none().equals(Option.none()));
    }
}
