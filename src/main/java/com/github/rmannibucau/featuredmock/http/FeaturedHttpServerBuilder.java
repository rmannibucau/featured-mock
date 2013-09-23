package com.github.rmannibucau.featuredmock.http;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;

public class FeaturedHttpServerBuilder {
    private String host = "localhost";
    private int port = 8080;
    private int threads = 8;

    private boolean ssl = false;
    private String protocol = "TLS";
    private SecureRandom secureRandom;
    private TrustManager[] trustManagers;
    private KeyManager[] keyManagers;

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

    public FeaturedHttpServer build() {
        return new DefaultFeaturedHttpServer(host, port, threads, ssl, protocol, secureRandom, trustManagers, keyManagers);
    }
}
