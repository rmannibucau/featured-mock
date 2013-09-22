package com.github.rmannibucau.featuredmock.mock.unmarshaller.jaxb;

import com.github.rmannibucau.featuredmock.mock.unmarshaller.Unmarshaller;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JaxbUnmarshaller implements Unmarshaller {
    private static final ConcurrentMap<Class<?>, JAXBContext> CACHE = new ConcurrentHashMap<Class<?>, JAXBContext>();

    @Override
    public <T> T unmarshall(final Class<T> clazz, final InputStream is) throws IOException {
        try {
            JAXBContext ctx = CACHE.get(clazz);
            if (ctx == null) {
                ctx = JAXBContext.newInstance(clazz);
                CACHE.putIfAbsent(clazz, ctx);
            }

            final Object obj = ctx.createUnmarshaller().unmarshal(is);
            if (JAXBElement.class.isInstance(obj)) {
                return ((JAXBElement<T>) obj).getValue();
            }
            return clazz.cast(obj);
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
