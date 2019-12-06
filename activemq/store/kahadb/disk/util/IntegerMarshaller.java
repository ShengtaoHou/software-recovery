// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class IntegerMarshaller implements Marshaller<Integer>
{
    public static final IntegerMarshaller INSTANCE;
    
    @Override
    public void writePayload(final Integer object, final DataOutput dataOut) throws IOException {
        dataOut.writeInt(object);
    }
    
    @Override
    public Integer readPayload(final DataInput dataIn) throws IOException {
        return dataIn.readInt();
    }
    
    @Override
    public int getFixedSize() {
        return 4;
    }
    
    @Override
    public Integer deepCopy(final Integer source) {
        return source;
    }
    
    @Override
    public boolean isDeepCopySupported() {
        return true;
    }
    
    static {
        INSTANCE = new IntegerMarshaller();
    }
}
