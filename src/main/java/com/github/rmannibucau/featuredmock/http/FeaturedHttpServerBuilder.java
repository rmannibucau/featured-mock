package com.github.rmannibucau.featuredmock.http;

public class FeaturedHttpServerBuilder {
    private String host = "localhost";
    private int port = 8080;
    private int threads = 8;

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

    public FeaturedHttpServer build() {
        return new DefaultFeaturedHttpServer(host, port, threads);
    }
}
