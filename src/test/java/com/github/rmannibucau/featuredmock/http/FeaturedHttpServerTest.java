package com.github.rmannibucau.featuredmock.http;

import com.github.rmannibucau.featuredmock.util.IOs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class FeaturedHttpServerTest {
    private FeaturedHttpServer server;

    @Before
    public void startNewServer() {
        server = new FeaturedHttpServerBuilder().port(1234).build().start();
    }

    @After
    public void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void json() throws Exception {
        assertURL("http://localhost:1234/foo", "{ \"json\": \"great\" }");
    }

    @Test
    public void xml() throws Exception {
        assertURL("http://localhost:1234/bar", "<xml />");
    }

    @Test
    public void xmlWithExtension() throws Exception {
        assertURL("http://localhost:1234/bar.xml", "<xml />");
    }

    private static void assertURL(final String targetUrl, final String content) throws IOException {
        final URL url = new URL(targetUrl);
        final InputStream is = url.openStream();
        try {
            final String output = new String(IOs.slurp(is));
            assertEquals(content, output.trim());
        } finally {
            is.close();
        }
    }
}
