// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.mqtt.codec.MQTTFrame;
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
import org.apache.activemq.wireformat.WireFormat;

public class MQTTWireFormat implements WireFormat
{
    static final int MAX_MESSAGE_LENGTH = 268435456;
    private int version;
    
    public MQTTWireFormat() {
        this.version = 1;
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
    
    @Override
    public void marshal(final Object command, final DataOutput dataOut) throws IOException {
        final MQTTFrame frame = (MQTTFrame)command;
        dataOut.write(frame.header());
        int remaining = 0;
        for (final Buffer buffer : frame.buffers) {
            remaining += buffer.length;
        }
        do {
            byte digit = (byte)(remaining & 0x7F);
            remaining >>>= 7;
            if (remaining > 0) {
                digit |= (byte)128;
            }
            dataOut.write(digit);
        } while (remaining > 0);
        for (final Buffer buffer : frame.buffers) {
            dataOut.write(buffer.data, buffer.offset, buffer.length);
        }
    }
    
    @Override
    public Object unmarshal(final DataInput dataIn) throws IOException {
        final byte header = dataIn.readByte();
        int multiplier = 1;
        int length = 0;
        byte digit;
        do {
            digit = dataIn.readByte();
            length += (digit & 0x7F) * multiplier;
            multiplier <<= 7;
        } while ((digit & 0x80) != 0x0);
        if (length < 0) {
            return null;
        }
        if (length > 268435456) {
            throw new IOException("The maximum message length was exceeded");
        }
        if (length > 0) {
            final byte[] data = new byte[length];
            dataIn.readFully(data);
            final Buffer body = new Buffer(data);
            return new MQTTFrame(body).header(header);
        }
        return new MQTTFrame().header(header);
    }
    
    @Override
    public void setVersion(final int version) {
        this.version = version;
    }
    
    @Override
    public int getVersion() {
        return this.version;
    }
}
