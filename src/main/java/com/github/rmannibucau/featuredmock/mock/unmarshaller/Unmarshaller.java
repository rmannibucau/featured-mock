package com.github.rmannibucau.featuredmock.mock.unmarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public interface Unmarshaller {
    Object unmarshall(Type clazz, InputStream is) throws IOException;
    boolean accept(String extension);
}
