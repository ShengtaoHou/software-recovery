// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class StringMarshaller implements Marshaller<String>
{
    public static final StringMarshaller INSTANCE;
    
    @Override
    public void writePayload(final String object, final DataOutput dataOut) throws IOException {
        dataOut.writeUTF(object);
    }
    
    @Override
    public String readPayload(final DataInput dataIn) throws IOException {
        return dataIn.readUTF();
    }
    
    @Override
    public int getFixedSize() {
        return -1;
    }
    
    @Override
    public String deepCopy(final String source) {
        return source;
    }
    
    @Override
    public boolean isDeepCopySupported() {
        return true;
    }
    
    static {
        INSTANCE = new StringMarshaller();
    }
}
