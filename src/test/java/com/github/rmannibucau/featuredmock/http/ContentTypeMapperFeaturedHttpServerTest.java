package com.github.rmannibucau.featuredmock.http;

import com.github.rmannibucau.featuredmock.util.IOs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ContentTypeMapperFeaturedHttpServerTest {
    private FeaturedHttpServer server;

    @Before
    public void startNewServer() {
        server = new FeaturedHttpServerBuilder().mapper(new ContentTypeMapper() {
            @Override
            public boolean handle(final String accept) {
                return "test".equals(accept);
            }

            @Override
            public String contentType(final String accept) {
                return "awesome";
            }

            @Override
            public String extension(final String accept) {
                return "ctm";
            }
        }).port(1234).build().start();
    }

    @After
    public void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void json() throws Exception {
        assertURL("http://localhost:1234/test", "ok");
    }

    private static void assertURL(final String targetUrl, final String content) throws IOException {
        final URL url = new URL(targetUrl);
        final HttpURLConnection connection = HttpURLConnection.class.cast(url.openConnection());
        connection.addRequestProperty("accept", "test");
        final InputStream is = connection.getInputStream();
        try {
            final String output = new String(IOs.slurp(is));
            assertEquals(content, output.trim());
            assertEquals("awesome", connection.getHeaderField("Content-Type"));
        } finally {
            is.close();
        }
    }
}
