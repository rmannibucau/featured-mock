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

import java.security.SecureRandom;
import java.util.Collection;
import java.util.LinkedList;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;

public class FeaturedHttpServerBuilder {
    private String host = "localhost";
    private int port = 8080;
    private int threads = 8;

    private boolean ssl = false;
    private String protocol = "TLS";
    private SecureRandom secureRandom;
    private TrustManager[] trustManagers;
    private KeyManager[] keyManagers;
    private Collection<ContentTypeMapper> mappers = new LinkedList<ContentTypeMapper>();
    private RequestObserver observer;

    public FeaturedHttpServerBuilder observer(final RequestObserver observer) {
        this.observer = observer;
        return this;
    }

    public FeaturedHttpServerBuilder host(final String host) {
        this.host = host;
        return this;
    }

    public FeaturedHttpServerBuilder port(final int port) {
        this.port = port;
        return this;
    }

    public FeaturedHttpServerBuilder threads(final int threads) {
        this.threads = threads;
        return this;
    }

    public FeaturedHttpServerBuilder ssl() {
        this.ssl = true;
        return this;
    }

    public FeaturedHttpServerBuilder secureRandom(final SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    public FeaturedHttpServerBuilder trustManagers(final TrustManager[] trustManagers) {
        this.trustManagers = trustManagers;
        return this;
    }

    public FeaturedHttpServerBuilder keyManagers(final KeyManager[] keyManagers) {
        this.keyManagers = keyManagers;
        return this;
    }

    public FeaturedHttpServerBuilder protocol(final String protocol) {
        this.protocol = protocol;
        return this;
    }

    public FeaturedHttpServerBuilder mapper(final ContentTypeMapper mapper) {
        mappers.add(mapper);
        return this;
    }

    public FeaturedHttpServer build() {
        final SSLEngine engine;
        if (ssl) {
            SSLContext clientContext;
            try {
                clientContext = SSLContext.getInstance(protocol);
                clientContext.init(keyManagers, trustManagers, secureRandom);
            } catch (final Exception e) {
                throw new Error(
                    "Failed to initialize the client-side SSLContext", e);
            }

            engine = clientContext.createSSLEngine();
            engine.setUseClientMode(true);
        } else {
            engine = null;
        }

        return new DefaultFeaturedHttpServer(
                host, port, threads,
                mappers.toArray(new ContentTypeMapper[mappers.size()]), engine, observer);
    }
}
