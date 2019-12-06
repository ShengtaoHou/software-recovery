// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.codehaus.jackson.map.ObjectMapper;

public final class ClusteredSupport$
{
    public static final ClusteredSupport$ MODULE$;
    private final ObjectMapper DEFAULT_MAPPER;
    
    static {
        new ClusteredSupport$();
    }
    
    public ObjectMapper DEFAULT_MAPPER() {
        return this.DEFAULT_MAPPER;
    }
    
    public <T> T decode(final Class<T> t, final byte[] buffer, final ObjectMapper mapper) {
        return this.decode(t, new ByteArrayInputStream(buffer), mapper);
    }
    
    public <T> T decode(final Class<T> t, final InputStream in, final ObjectMapper mapper) {
        return (T)mapper.readValue(in, (Class)t);
    }
    
    public <T> ObjectMapper decode$default$3() {
        return this.DEFAULT_MAPPER();
    }
    
    public byte[] encode(final Object value, final ObjectMapper mapper) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.encode(value, baos, mapper);
        return baos.toByteArray();
    }
    
    public void encode(final Object value, final OutputStream out, final ObjectMapper mapper) {
        mapper.writeValue(out, value);
    }
    
    public ObjectMapper encode$default$2() {
        return this.DEFAULT_MAPPER();
    }
    
    private ClusteredSupport$() {
        MODULE$ = this;
        this.DEFAULT_MAPPER = new ObjectMapper();
    }
}
