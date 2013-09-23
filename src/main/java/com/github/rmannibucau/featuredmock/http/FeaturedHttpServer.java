package com.github.rmannibucau.featuredmock.http;

public interface FeaturedHttpServer {
    FeaturedHttpServer start();
    void stop();
    int getPort();
}
