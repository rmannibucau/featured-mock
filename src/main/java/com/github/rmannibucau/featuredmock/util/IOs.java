package com.github.rmannibucau.featuredmock.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class IOs {
    private static final int BUFFER_SIZE = 1024;

    public static byte[] slurp(final InputStream is) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        return baos.toByteArray();
    }

    private IOs() {
        // no-op
    }
}
