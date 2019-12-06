// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.wireformat;

import org.apache.activemq.util.ClassLoadingAwareObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import java.io.IOException;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.util.ByteSequence;

public class ObjectStreamWireFormat implements WireFormat
{
    @Override
    public ByteSequence marshal(final Object command) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream ds = new DataOutputStream(baos);
        this.marshal(command, ds);
        ds.close();
        return baos.toByteSequence();
    }
    
    @Override
    public Object unmarshal(final ByteSequence packet) throws IOException {
        return this.unmarshal(new DataInputStream(new ByteArrayInputStream(packet)));
    }
    
    @Override
    public void marshal(final Object command, final DataOutput ds) throws IOException {
        final ObjectOutputStream out = new ObjectOutputStream((OutputStream)ds);
        out.writeObject(command);
        out.flush();
        out.reset();
    }
    
    @Override
    public Object unmarshal(final DataInput ds) throws IOException {
        try {
            final ClassLoadingAwareObjectInputStream in = new ClassLoadingAwareObjectInputStream((InputStream)ds);
            final Object command = in.readObject();
            in.close();
            return command;
        }
        catch (ClassNotFoundException e) {
            throw (IOException)new IOException("unmarshal failed: " + e).initCause(e);
        }
    }
    
    @Override
    public void setVersion(final int version) {
    }
    
    @Override
    public int getVersion() {
        return 0;
    }
}
