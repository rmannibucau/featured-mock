package com.github.rmannibucau.featuredmock.http;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class FeaturedThreadFactory implements ThreadFactory {
    private static final AtomicInteger ID = new AtomicInteger(1);

    private final AtomicInteger threadId = new AtomicInteger(1);

    @Override
    public Thread newThread(final Runnable r) {
        final Thread t = new Thread(r, "featured-http-" + ID.getAndIncrement() + "-" + threadId.getAndIncrement());
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        return t;
    }
}
