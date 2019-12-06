// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import java.nio.channels.WritableByteChannel;
import org.fusesource.hawtbuf.Buffer;
import java.nio.channels.Channels;
import java.nio.ByteBuffer;
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

public class AmqpWireFormat implements WireFormat
{
    public static final int DEFAULT_MAX_FRAME_SIZE = 1048576;
    private int version;
    private long maxFrameSize;
    boolean magicRead;
    
    public AmqpWireFormat() {
        this.version = 1;
        this.maxFrameSize = 1048576L;
        this.magicRead = false;
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
        if (command instanceof ByteBuffer) {
            final ByteBuffer buffer = (ByteBuffer)command;
            if (dataOut instanceof OutputStream) {
                final WritableByteChannel channel = Channels.newChannel((OutputStream)dataOut);
                channel.write(buffer);
            }
            else {
                while (buffer.hasRemaining()) {
                    dataOut.writeByte(buffer.get());
                }
            }
        }
        else {
            final Buffer frame = (Buffer)command;
            frame.writeTo(dataOut);
        }
    }
    
    @Override
    public Object unmarshal(final DataInput dataIn) throws IOException {
        if (!this.magicRead) {
            final Buffer magic = new Buffer(8);
            magic.readFrom(dataIn);
            this.magicRead = true;
            return new AmqpHeader(magic);
        }
        final int size = dataIn.readInt();
        if (size > this.maxFrameSize) {
            throw new AmqpProtocolException("Frame size exceeded max frame length.");
        }
        final Buffer frame = new Buffer(size);
        frame.bigEndianEditor().writeInt(size);
        frame.readFrom(dataIn);
        frame.clear();
        return frame;
    }
    
    @Override
    public void setVersion(final int version) {
        this.version = version;
    }
    
    @Override
    public int getVersion() {
        return this.version;
    }
    
    public long getMaxFrameSize() {
        return this.maxFrameSize;
    }
    
    public void setMaxFrameSize(final long maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }
}
