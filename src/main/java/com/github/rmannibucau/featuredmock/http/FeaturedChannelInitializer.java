package com.github.rmannibucau.featuredmock.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

class FeaturedChannelInitializer extends ChannelInitializer<SocketChannel> {
    public static final ChannelInitializer<SocketChannel> INSTANCE = new FeaturedChannelInitializer();

    private FeaturedChannelInitializer() {
        // no-io
    }

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline()
            .addLast("decoder", new HttpRequestDecoder())
            .addLast("aggregator", new HttpObjectAggregator(Integer.MAX_VALUE))
            .addLast("encoder", new HttpResponseEncoder())
            .addLast("chunked-writer", new ChunkedWriteHandler())
            .addLast("featured-mock-server", FeaturedHandler.INSTANCE);
    }
}
