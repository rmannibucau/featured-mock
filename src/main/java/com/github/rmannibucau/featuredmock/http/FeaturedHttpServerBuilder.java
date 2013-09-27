package com.github.rmannibucau.featuredmock.http;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.LinkedList;

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

        return new DefaultFeaturedHttpServer(host, port, threads, mappers.toArray(new ContentTypeMapper[mappers.size()]), engine);
    }
}
