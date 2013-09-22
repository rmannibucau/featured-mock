package com.github.rmannibucau.featuredmock.mock.unmarshaller;

import java.io.IOException;
import java.io.InputStream;

public interface Unmarshaller {
    <T> T unmarshall(Class<T> clazz, InputStream is) throws IOException;
    boolean accept(String extension);
}
