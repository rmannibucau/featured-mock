package com.github.rmannibucau.featuredmock.mock.unmarshaller.jaxb;

import com.github.rmannibucau.featuredmock.mock.unmarshaller.Unmarshaller;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JaxbUnmarshaller implements Unmarshaller {
    private static final ConcurrentMap<Type, JAXBContext> CACHE = new ConcurrentHashMap<Type, JAXBContext>();

    @Override
    public Object unmarshall(final Type clazz, final InputStream is) throws IOException {
        try {
            JAXBContext ctx = CACHE.get(clazz);
            if (ctx == null) {
                ctx = JAXBContext.newInstance(Class.class.cast(clazz));
                CACHE.putIfAbsent(clazz, ctx);
            }

            final Object obj = ctx.createUnmarshaller().unmarshal(is);
            if (JAXBElement.class.isInstance(obj)) {
                return JAXBElement.class.cast(obj).getValue();
            }
            return obj;
        } catch (final JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean accept(final String extension) {
        return ".xml".equals(extension);
    }
}
