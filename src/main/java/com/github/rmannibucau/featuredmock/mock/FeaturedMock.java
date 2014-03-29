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
package com.github.rmannibucau.featuredmock.mock;

import com.github.rmannibucau.featuredmock.mock.unmarshaller.Unmarshaller;
import com.github.rmannibucau.featuredmock.util.Extensions;
import com.github.rmannibucau.featuredmock.util.IOs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.LinkedList;

public final class FeaturedMock {
    private static final Unmarshaller[] DEFAULT_UNMARSHALLERS;
    static {
        final Collection<Unmarshaller> list = new LinkedList<Unmarshaller>();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (final String name : new String[]{
            "com.github.rmannibucau.featuredmock.mock.unmarshaller.jackson.JacksonUnmarshaller",
            "com.github.rmannibucau.featuredmock.mock.unmarshaller.jaxb.JaxbUnmarshaller"
        }) {
            try {
                list.add(Unmarshaller.class.cast(Class.forName(name, true, classLoader).newInstance()));
            } catch (final Throwable th) {
                // no-op
            }
        }
        DEFAULT_UNMARSHALLERS = list.toArray(new Unmarshaller[list.size()]);
    }

    public static <T> T mock(final Class<T> mock) {
        return mock(mock, DEFAULT_UNMARSHALLERS);
    }

    public static <T> T mock(final Class<T> mock, final Unmarshaller... unmarshallers) {
        return mock.cast(Proxy.newProxyInstance(mock.getClassLoader(), new Class<?>[] { mock }, new FeaturedMockHandler(unmarshallers)));
    }

    private FeaturedMock() {
        // no-op
    }

    private static class FeaturedMockHandler implements InvocationHandler, Extensions {
        private final Unmarshaller[] unmarshallers;

        public FeaturedMockHandler(final Unmarshaller[] unmarshallers) {
            this.unmarshallers = unmarshallers;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            final String resource = method.getDeclaringClass().getName().replace(".", "/") + "/" + method.getName();

            for (final String extension : EXTENSIONS) {
                final InputStream is = contextClassLoader.getResourceAsStream(resource + extension);
                if (is == null) {
                    continue;
                }

                final byte[] slurp;
                try {
                    slurp = IOs.slurp(is);
                } finally {
                    is.close();
                }

                for (final Unmarshaller unmarshaller : unmarshallers) {
                    if (unmarshaller.accept(extension)) {
                        return unmarshaller.unmarshall(method.getGenericReturnType(), new ByteArrayInputStream(slurp));
                    }
                }
            }

            return null; // null is the default, let it be called, it is just not yet mocked
        }
    }
}
