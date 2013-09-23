package com.github.rmannibucau.featuredmock.mock.unmarshaller.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rmannibucau.featuredmock.mock.unmarshaller.Unmarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JacksonUnmarshaller implements Unmarshaller {
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Object unmarshall(final Type type, final InputStream is) throws IOException {
        if (ParameterizedType.class.isInstance(type)) {
            return MAPPER.readValue(is, new ParameterizedTypeReference(type));
        }
        return MAPPER.readValue(is, Class.class.cast(type));
    }

    @Override
    public boolean accept(final String extension) {
        return ".json".equals(extension);
    }

    private static class ParameterizedTypeReference extends TypeReference<Object> {
        private final Type type;

        public ParameterizedTypeReference(final Type argument) {
            type = argument;
        }

        @Override
        public Type getType() {
            return type;
        }
    }
}
