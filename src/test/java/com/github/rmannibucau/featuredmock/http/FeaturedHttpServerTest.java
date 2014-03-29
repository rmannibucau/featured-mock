/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    public void jsonWithQuery() throws Exception {
        assertURL("http://localhost:1234/foo?a=b", "{ \"json\": \"great\" }");
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
