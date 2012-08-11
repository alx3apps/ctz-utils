package ru.concerteza.util.db.springjdbc.named;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Test;
import ru.concerteza.util.json.JsonObjectAsMap;

import javax.inject.Named;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * User: alexey
 * Date: 8/9/12
 */
public class NamedConstructorFunctionTest {
    private static final Map<String, ?> DATA = ImmutableMap.<String, Object>builder()
            .put("foo", "42")
            .put("child", ImmutableMap.of("bar", "40"))
            .put("nodes", ImmutableList.of(ImmutableMap.of("val", 41), ImmutableMap.of("val", 42)))
            .build();

//    @Test
    public void test() {
        NamedConstructorFunction<Parent> fu = NamedConstructorFunction.of(Parent.class);
        Parent pa = fu.apply(DATA);
        assertNotNull("Creation fail", pa);
        assertEquals("Parent field fail", "42", pa.foo);
        assertTrue("Child fail", pa.child.isPresent());
        assertEquals("Child field fail", "40", pa.child.get().bar);
        assertEquals("Nodes size fail", 2, pa.nodes.size());
        assertEquals("Nodes fail", 41, pa.nodes.get(0).val);
        assertEquals("Nodes fail", 42, pa.nodes.get(1).val);
    }

    @Test
    public void testJson() {
        String json = new Gson().toJson(DATA);
        Map<String, ?> parsed = JsonObjectAsMap.of(json);
        NamedConstructorFunction<Parent> fu = NamedConstructorFunction.of(Parent.class);
        Parent pa = fu.apply(parsed);
        assertNotNull("Creation fail", pa);
        assertEquals("Parent field fail", "42", pa.foo);
        assertTrue("Child fail", pa.child.isPresent());
        assertEquals("Child field fail", "40", pa.child.get().bar);
        assertEquals("Nodes size fail", 2, pa.nodes.size());
        assertEquals("Nodes fail", 41, pa.nodes.get(0).val);
        assertEquals("Nodes fail", 42, pa.nodes.get(1).val);
    }

    private static class Parent {
        private final String foo;
        private final Optional<Child> child;
        private final List<Node> nodes;

        private Parent(@Named("foo") String foo, @NamedGenericRef(name = "child", type = Child.class) Optional<Child> child,
                       @NamedGenericRef(name = "nodes", type = Node.class) List<Node> nodes) {
            this.foo = foo;
            this.child = child;
            this.nodes = nodes;
        }
    }

    private static class Child {
        private final String bar;

        private Child(@Named("bar") String bar) {
            this.bar = bar;
        }
    }

    private static class Node {
        private final int val;

        private Node(@Named("val") Number val) {
            this.val = val.intValue();
        }
    }
}
