package com.github.rmannibucau.featuredmock.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

class DefaultFeaturedHttpServer implements FeaturedHttpServer {
    private static final Logger LOGGER = Logger.getLogger(FeaturedHttpServer.class.getName());

    private final String host;
    private final int port;
    private final int threads;
    private final boolean ssl;
    private final String protocol;
    private final SecureRandom secureRandom;
    private final TrustManager[] trustManagers;
    private final KeyManager[] keyManagers;

    private NioEventLoopGroup workerGroup;

    public DefaultFeaturedHttpServer(final String host, final int port, final int threads,
                                     final boolean ssl, final String protocol, final SecureRandom secureRandom,
                                     final TrustManager[] trustManagers, final KeyManager[] keyManagers) {
        this.host = host;
        if (port <= 0) { // generate a port
            this.port = findNextAvailablePort();
        } else {
            this.port = port;
        }
        this.threads = Math.max(threads, 1);
        this.ssl = ssl;
        this.protocol = protocol;
        this.secureRandom = secureRandom;
        this.trustManagers = trustManagers;
        this.keyManagers = keyManagers;
    }

    @Override
    public int getPort() {
        return port;
    }

    private static int findNextAvailablePort() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
            return serverSocket.getLocalPort();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (final IOException e) {
                    // no-op
                }
            }
        }
        return 0;
    }

    @Override
    public FeaturedHttpServer start() {
        workerGroup = new NioEventLoopGroup(threads, new FeaturedThreadFactory());

        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_SNDBUF, 1024)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new FeaturedChannelInitializer(ssl, protocol, secureRandom, trustManagers, keyManagers))
                .bind(host, port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        LOGGER.severe("Can't start HTTP server");
                    } else {
                        LOGGER.info(String.format("Server started on http://%s:%s", host, port));
                    }
                }
            }).sync();
        } catch (final InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return this;
    }

    @Override
    public void stop() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            LOGGER.info(String.format("Server http://%s:%s stopped", host, port));
        }
    }
}
