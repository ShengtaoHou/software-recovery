// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import java.io.OutputStream;
import org.fusesource.hawtbuf.ByteArrayOutputStream;
import java.io.InputStream;
import org.fusesource.hawtbuf.Buffer;
import org.codehaus.jackson.map.ObjectMapper;

public final class JsonCodec$
{
    public static final JsonCodec$ MODULE$;
    private final ObjectMapper mapper;
    
    static {
        new JsonCodec$();
    }
    
    public final ObjectMapper mapper() {
        return this.mapper;
    }
    
    public <T> T decode(final Buffer buffer, final Class<T> clazz) {
        final ClassLoader original = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            return (T)this.mapper().readValue((InputStream)buffer.in(), (Class)clazz);
        }
        finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
    
    public <T> T decode(final InputStream is, final Class<T> clazz) {
        final ClassLoader original = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            return (T)this.mapper().readValue(is, (Class)clazz);
        }
        finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
    
    public Buffer encode(final Object value) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.mapper().writeValue((OutputStream)baos, value);
        return baos.toBuffer();
    }
    
    private JsonCodec$() {
        MODULE$ = this;
        this.mapper = new ObjectMapper();
    }
}
