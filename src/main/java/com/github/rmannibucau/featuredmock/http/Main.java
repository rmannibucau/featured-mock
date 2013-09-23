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
