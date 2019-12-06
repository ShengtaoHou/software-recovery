// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

public class BytesMarshaller implements Marshaller<byte[]>
{
    @Override
    public void writePayload(final byte[] data, final DataOutput dataOut) throws IOException {
        dataOut.writeInt(data.length);
        dataOut.write(data);
    }
    
    @Override
    public byte[] readPayload(final DataInput dataIn) throws IOException {
        final int size = dataIn.readInt();
        final byte[] data = new byte[size];
        dataIn.readFully(data);
        return data;
    }
    
    @Override
    public int getFixedSize() {
        return -1;
    }
    
    @Override
    public byte[] deepCopy(final byte[] source) {
        final byte[] rc = new byte[source.length];
        System.arraycopy(source, 0, rc, 0, source.length);
        return rc;
    }
    
    @Override
    public boolean isDeepCopySupported() {
        return true;
    }
}
