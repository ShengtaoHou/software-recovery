// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

public final class BufferOutputStream extends OutputStream
{
    byte[] buffer;
    int offset;
    int limit;
    int pos;
    
    public BufferOutputStream(final int size) {
        this(new byte[size]);
    }
    
    public BufferOutputStream(final byte[] buffer) {
        this.buffer = buffer;
        this.limit = buffer.length;
    }
    
    public BufferOutputStream(final Buffer data) {
        this.buffer = data.data;
        final int offset = data.offset;
        this.offset = offset;
        this.pos = offset;
        this.limit = data.offset + data.length;
    }
    
    @Override
    public void write(final int b) throws IOException {
        final int newPos = this.pos + 1;
        this.checkCapacity(newPos);
        this.buffer[this.pos] = (byte)b;
        this.pos = newPos;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        final int newPos = this.pos + len;
        this.checkCapacity(newPos);
        System.arraycopy(b, off, this.buffer, this.pos, len);
        this.pos = newPos;
    }
    
    public Buffer getNextBuffer(final int len) throws IOException {
        final int newPos = this.pos + len;
        this.checkCapacity(newPos);
        return new Buffer(this.buffer, this.pos, len);
    }
    
    private void checkCapacity(final int minimumCapacity) throws IOException {
        if (minimumCapacity > this.limit) {
            throw new EOFException("Buffer limit reached.");
        }
    }
    
    public void reset() {
        this.pos = this.offset;
    }
    
    public Buffer toBuffer() {
        return new Buffer(this.buffer, this.offset, this.pos);
    }
    
    public byte[] toByteArray() {
        return this.toBuffer().toByteArray();
    }
    
    public int size() {
        return this.offset - this.pos;
    }
}
