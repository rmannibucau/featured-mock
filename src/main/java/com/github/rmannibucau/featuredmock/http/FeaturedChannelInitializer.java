package com.github.rmannibucau.featuredmock.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import java.security.Security;

class FeaturedChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final boolean ssl;
    private final String protocol;
    private final SecureRandom secureRandom;
    private final TrustManager[] trustManagers;
    private final KeyManager[] keyManagers;

    public FeaturedChannelInitializer(final boolean ssl, final String protocol, final SecureRandom secureRandom,
                                      final TrustManager[] trustManagers, final KeyManager[] keyManagers) {
        this.ssl = ssl;
        this.protocol = protocol;
        this.secureRandom = secureRandom;
        this.trustManagers = trustManagers;
        this.keyManagers = keyManagers;
    }

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        if (ssl) {
            String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
            if (algorithm == null) {
                algorithm = "SunX509";
            }

            SSLContext clientContext;
            try {
                clientContext = SSLContext.getInstance(protocol);
                clientContext.init(keyManagers, trustManagers, secureRandom);
            } catch (Exception e) {
                throw new Error(
                    "Failed to initialize the client-side SSLContext", e);
            }

            final SSLEngine engine = clientContext.createSSLEngine();
            engine.setUseClientMode(true);

            pipeline.addLast("ssl", new SslHandler(engine));
        }

        pipeline
            .addLast("decoder", new HttpRequestDecoder())
            .addLast("aggregator", new HttpObjectAggregator(Integer.MAX_VALUE))
            .addLast("encoder", new HttpResponseEncoder())
            .addLast("chunked-writer", new ChunkedWriteHandler())
            .addLast("featured-mock-server", FeaturedHandler.INSTANCE);
    }
}
