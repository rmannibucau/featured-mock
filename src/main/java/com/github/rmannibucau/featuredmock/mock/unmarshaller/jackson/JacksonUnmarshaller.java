package com.github.rmannibucau.featuredmock.mock.unmarshaller.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rmannibucau.featuredmock.mock.unmarshaller.Unmarshaller;

import java.io.IOException;
import java.io.InputStream;

public class JacksonUnmarshaller implements Unmarshaller {
    private static final ObjectMapper MAPPER = new ObjectMapper()
                                                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public <T> T unmarshall(final Class<T> clazz, final InputStream is) throws IOException {
        return MAPPER.readValue(is, clazz);
    }

    @Override
    public boolean accept(final String extension) {
        return ".json".equals(extension);
    }
}
