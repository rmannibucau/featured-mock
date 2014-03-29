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
