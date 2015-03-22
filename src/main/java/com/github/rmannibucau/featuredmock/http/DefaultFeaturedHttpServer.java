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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

class DefaultFeaturedHttpServer implements FeaturedHttpServer {
    private static final Logger LOGGER = Logger.getLogger(FeaturedHttpServer.class.getName());

    private final String host;
    private final int port;
    private final int threads;
    private final ContentTypeMapper[] mappers;
    private final SSLEngine engine;
    private final RequestObserver observer;

    private NioEventLoopGroup workerGroup;

    public DefaultFeaturedHttpServer(final String host, final int port, final int threads,
                                     final ContentTypeMapper[] mappers, final SSLEngine engine,
                                     final RequestObserver observer) {
        this.host = host;
        if (port <= 0) { // generate a port
            this.port = findNextAvailablePort();
        } else {
            this.port = port;
        }
        this.threads = Math.max(threads, 1);
        this.engine = engine;
        this.mappers = mappers;
        this.observer = observer;
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
                .childHandler(new FeaturedChannelInitializer(mappers, engine, observer))
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
