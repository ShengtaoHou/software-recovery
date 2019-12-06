// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.util;

import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.util.ByteSequence;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.Reader;
import java.io.IOException;
import org.apache.activemq.wireformat.WireFormat;

public abstract class TextWireFormat implements WireFormat
{
    public abstract Object unmarshalText(final String p0) throws IOException;
    
    public abstract Object unmarshalText(final Reader p0) throws IOException;
    
    public abstract String marshalText(final Object p0) throws IOException;
    
    @Override
    public void marshal(final Object command, final DataOutput out) throws IOException {
        final String text = this.marshalText(command);
        final byte[] utf8 = text.getBytes("UTF-8");
        out.writeInt(utf8.length);
        out.write(utf8);
    }
    
    @Override
    public Object unmarshal(final DataInput in) throws IOException {
        final int length = in.readInt();
        final byte[] utf8 = new byte[length];
        in.readFully(utf8);
        final String text = new String(utf8, "UTF-8");
        return this.unmarshalText(text);
    }
    
    @Override
    public ByteSequence marshal(final Object command) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        this.marshal(command, dos);
        dos.close();
        return baos.toByteSequence();
    }
    
    @Override
    public Object unmarshal(final ByteSequence packet) throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(packet);
        final DataInputStream dis = new DataInputStream(stream);
        return this.unmarshal(dis);
    }
    
    public boolean inReceive() {
        return false;
    }
}
