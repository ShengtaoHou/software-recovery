// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;

public class ObjectMarshaller extends VariableMarshaller<Object>
{
    @Override
    public void writePayload(final Object object, final DataOutput dataOut) throws IOException {
        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        final ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
        objectOut.writeObject(object);
        objectOut.close();
        final byte[] data = bytesOut.toByteArray();
        dataOut.writeInt(data.length);
        dataOut.write(data);
    }
    
    @Override
    public Object readPayload(final DataInput dataIn) throws IOException {
        final int size = dataIn.readInt();
        final byte[] data = new byte[size];
        dataIn.readFully(data);
        final ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
        final ObjectInputStream objectIn = new ObjectInputStream(bytesIn);
        try {
            return objectIn.readObject();
        }
        catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }
}
