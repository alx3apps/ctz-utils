package ru.concerteza.util.json;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: alexey
 * Date: 8/16/12
 */
public class OptionalTypeAdapterFactoryTest {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
            .create();

    @Test
    public void testSerialize() {
        String absent = GSON.toJson(new Foo(Optional.<String>absent()));
        assertEquals("Absent fail", "{}", absent);
        String present = GSON.toJson(new Foo(Optional.of("foo")));
        assertEquals("Present fail", "{\"bar\":\"foo\"}", present);
    }

    @Test
    public void testDeserialize() {
        Foo absent = GSON.fromJson("{}", Foo.class);
        assertNotNull("Creation fail", absent.bar);
        assertFalse("Absent fail", absent.bar.isPresent());
        Foo present = GSON.fromJson("{'bar': 'boo'}", Foo.class);
        assertTrue("Present fail", present.bar.isPresent());
        assertEquals("Value fail", "boo", present.bar.get());
    }

    @Test
    public void testBare() {
        assertEquals("\"foo\"", GSON.toJson(Optional.of("foo")));
        assertEquals("42", GSON.toJson(Optional.of(42)));
        assertEquals("42", GSON.toJson(Optional.of(new BigDecimal(42))));
        assertEquals("[]", GSON.toJson(Optional.of(ImmutableList.of())));
        assertEquals("null", GSON.toJson(Optional.absent()));
    }

    private static class Foo {
        private Optional<String> bar = Optional.absent();

        private Foo() {
        }

        private Foo(Optional<String> bar) {
            this.bar = bar;
        }
    }
}
