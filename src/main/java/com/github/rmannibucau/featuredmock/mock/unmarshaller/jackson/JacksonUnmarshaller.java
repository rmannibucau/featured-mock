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
