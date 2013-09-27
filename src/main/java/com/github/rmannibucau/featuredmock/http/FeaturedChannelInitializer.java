package com.github.rmannibucau.featuredmock.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLEngine;

class FeaturedChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final SslHandler sslHandler;
    private final FeaturedHandler handler;

    public FeaturedChannelInitializer(final ContentTypeMapper[] mappers, final SSLEngine engine) {
        if (engine == null) {
            this.sslHandler = null;
        } else {
            this.sslHandler = new SslHandler(engine);
        }
        this.handler = new FeaturedHandler(mappers);
    }

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        if (sslHandler != null) {
            pipeline.addLast("ssl", sslHandler);
        }

        pipeline
            .addLast("decoder", new HttpRequestDecoder())
            .addLast("aggregator", new HttpObjectAggregator(Integer.MAX_VALUE))
            .addLast("encoder", new HttpResponseEncoder())
            .addLast("chunked-writer", new ChunkedWriteHandler())
            .addLast("featured-mock-server", handler);
    }
}
