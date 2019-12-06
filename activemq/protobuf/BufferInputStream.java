// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.io.IOException;
import java.io.InputStream;

public final class BufferInputStream extends InputStream
{
    byte[] buffer;
    int limit;
    int pos;
    int mark;
    
    public BufferInputStream(final byte[] data) {
        this(data, 0, data.length);
    }
    
    public BufferInputStream(final Buffer sequence) {
        this(sequence.getData(), sequence.getOffset(), sequence.getLength());
    }
    
    public BufferInputStream(final byte[] data, final int offset, final int size) {
        this.buffer = data;
        this.mark = offset;
        this.pos = offset;
        this.limit = offset + size;
    }
    
    @Override
    public int read() throws IOException {
        if (this.pos < this.limit) {
            return this.buffer[this.pos++] & 0xFF;
        }
        return -1;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) {
        if (this.pos < this.limit) {
            len = Math.min(len, this.limit - this.pos);
            System.arraycopy(this.buffer, this.pos, b, off, len);
            this.pos += len;
            return len;
        }
        return -1;
    }
    
    public Buffer readBuffer(int len) {
        Buffer rc = null;
        if (this.pos < this.limit) {
            len = Math.min(len, this.limit - this.pos);
            rc = new Buffer(this.buffer, this.pos, len);
            this.pos += len;
        }
        return rc;
    }
    
    @Override
    public long skip(long len) throws IOException {
        if (this.pos < this.limit) {
            len = Math.min(len, this.limit - this.pos);
            if (len > 0L) {
                this.pos += (int)len;
            }
            return len;
        }
        return -1L;
    }
    
    @Override
    public int available() {
        return this.limit - this.pos;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public void mark(final int markpos) {
        this.mark = this.pos;
    }
    
    @Override
    public void reset() {
        this.pos = this.mark;
    }
}
