package com.github.rmannibucau.featuredmock.http;

import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestObserver {
    void onRequest(final FullHttpRequest request);
}
