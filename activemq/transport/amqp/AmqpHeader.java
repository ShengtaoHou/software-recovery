// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import org.fusesource.hawtbuf.Buffer;

public class AmqpHeader
{
    static final Buffer PREFIX;
    private Buffer buffer;
    
    public AmqpHeader() {
        this(new Buffer(new byte[] { 65, 77, 81, 80, 0, 1, 0, 0 }));
    }
    
    public AmqpHeader(final Buffer buffer) {
        this.setBuffer(buffer);
    }
    
    public int getProtocolId() {
        return this.buffer.get(4) & 0xFF;
    }
    
    public void setProtocolId(final int value) {
        this.buffer.data[this.buffer.offset + 4] = (byte)value;
    }
    
    public int getMajor() {
        return this.buffer.get(5) & 0xFF;
    }
    
    public void setMajor(final int value) {
        this.buffer.data[this.buffer.offset + 5] = (byte)value;
    }
    
    public int getMinor() {
        return this.buffer.get(6) & 0xFF;
    }
    
    public void setMinor(final int value) {
        this.buffer.data[this.buffer.offset + 6] = (byte)value;
    }
    
    public int getRevision() {
        return this.buffer.get(7) & 0xFF;
    }
    
    public void setRevision(final int value) {
        this.buffer.data[this.buffer.offset + 7] = (byte)value;
    }
    
    public Buffer getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final Buffer value) {
        if (!value.startsWith(AmqpHeader.PREFIX) || value.length() != 8) {
            throw new IllegalArgumentException("Not an AMQP header buffer");
        }
        this.buffer = value.buffer();
    }
    
    @Override
    public String toString() {
        return this.buffer.toString();
    }
    
    static {
        PREFIX = new Buffer(new byte[] { 65, 77, 81, 80 });
    }
}
