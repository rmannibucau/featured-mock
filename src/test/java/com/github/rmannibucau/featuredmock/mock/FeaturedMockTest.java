package com.github.rmannibucau.featuredmock.mock;

import com.github.rmannibucau.featuredmock.mock.bean.API;
import com.github.rmannibucau.featuredmock.mock.bean.Value;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FeaturedMockTest {
    private API api;

    @Before
    public void mock() {
        api = FeaturedMock.mock(API.class);
    }

    @Test
    public void jackson() {
        final Value value = api.jackson();
        assertEquals("uno", value.getAttr1());
        assertEquals("due", value.getAttr2());
    }

    @Test
    public void jacksonArray() {
        checkArray(api.jacksonArray());
    }

    @Test
    public void jacksonCollection() {
        final Collection<Value> values = api.jacksonCollection();
        assertEquals(2, values.size());
        checkArray(values.toArray(new Value[2]));
    }

    @Test
    public void jacksonMap() {
        final Map<String, Value> values = api.jacksonMap();
        assertEquals(2, values.size());
        for (int i = 0; i < 2; i++) {
            final int idx = i + 1;
            final String key = "item" + idx;
            assertTrue(values.containsKey(key));

            final Value value = values.get(key);
            assertEquals(idx + "1", value.getAttr1());
            assertEquals(idx + "2", value.getAttr2());
        }
    }
    @Test
    public void jaxb() {
        final Value value = api.jaxb();
        assertEquals("first", value.getAttr1());
        assertEquals("second", value.getAttr2());
    }

    private static void checkArray(final Value[] values) {
        assertNotNull(values);
        assertEquals(2, values.length);
        for (int i = 0; i < 2; i++) {
            final int idx = i + 1;
            assertEquals(idx + "1", values[i].getAttr1());
            assertEquals(idx + "2", values[i].getAttr2());
        }
    }
}
