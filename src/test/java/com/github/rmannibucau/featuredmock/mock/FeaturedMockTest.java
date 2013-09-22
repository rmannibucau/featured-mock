package com.github.rmannibucau.featuredmock.mock;

import com.github.rmannibucau.featuredmock.mock.bean.API;
import com.github.rmannibucau.featuredmock.mock.bean.Value;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
    public void jaxb() {
        final Value value = api.jaxb();
        assertEquals("first", value.getAttr1());
        assertEquals("second", value.getAttr2());
    }
}
