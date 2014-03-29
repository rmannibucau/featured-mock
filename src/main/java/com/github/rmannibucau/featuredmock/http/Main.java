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

import java.util.concurrent.CountDownLatch;

public final class Main {
    public static void main(final String[] args) {
        final FeaturedHttpServerBuilder builder = new FeaturedHttpServerBuilder();
        if (args != null) {
            final int length = args.length;
            for (int i = 0; i < length; i++) {
                final String name = args[i];
                if ("--host".equals(name) && (i + 1) < length) {
                    builder.host(args[i + 1]);
                } else if ("--port".equals(name) && (i + 1) < length) {
                    builder.port(Integer.parseInt(args[i + 1]));
                } else if ("--threads".equals(name) && (i + 1) < length) {
                    builder.threads(Integer.parseInt(args[i + 1]));
                }
            }
        }
        final CountDownLatch latch = new CountDownLatch(1);
        final FeaturedHttpServer server = builder.build();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stop();
                latch.countDown();
            }
        });
        server.start();
    }

    private Main() {
        // no-op
    }
}
