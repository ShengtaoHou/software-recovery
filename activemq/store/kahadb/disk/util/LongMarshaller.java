// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class LongMarshaller implements Marshaller<Long>
{
    public static final LongMarshaller INSTANCE;
    
    @Override
    public void writePayload(final Long object, final DataOutput dataOut) throws IOException {
        dataOut.writeLong(object);
    }
    
    @Override
    public Long readPayload(final DataInput dataIn) throws IOException {
        return dataIn.readLong();
    }
    
    @Override
    public int getFixedSize() {
        return 8;
    }
    
    @Override
    public Long deepCopy(final Long source) {
        return source;
    }
    
    @Override
    public boolean isDeepCopySupported() {
        return true;
    }
    
    static {
        INSTANCE = new LongMarshaller();
    }
}
