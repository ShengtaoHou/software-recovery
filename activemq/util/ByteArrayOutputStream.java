// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.OutputStream;

public class ByteArrayOutputStream extends OutputStream
{
    byte[] buffer;
    int size;
    
    public ByteArrayOutputStream() {
        this(1028);
    }
    
    public ByteArrayOutputStream(final int capacity) {
        this.buffer = new byte[capacity];
    }
    
    @Override
    public void write(final int b) {
        final int newsize = this.size + 1;
        this.checkCapacity(newsize);
        this.buffer[this.size] = (byte)b;
        this.size = newsize;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        final int newsize = this.size + len;
        this.checkCapacity(newsize);
        System.arraycopy(b, off, this.buffer, this.size, len);
        this.size = newsize;
    }
    
    private void checkCapacity(final int minimumCapacity) {
        if (minimumCapacity > this.buffer.length) {
            final byte[] b = new byte[Math.max(this.buffer.length << 1, minimumCapacity)];
            System.arraycopy(this.buffer, 0, b, 0, this.size);
            this.buffer = b;
        }
    }
    
    public void reset() {
        this.size = 0;
    }
    
    public ByteSequence toByteSequence() {
        return new ByteSequence(this.buffer, 0, this.size);
    }
    
    public byte[] toByteArray() {
        final byte[] rc = new byte[this.size];
        System.arraycopy(this.buffer, 0, rc, 0, this.size);
        return rc;
    }
    
    public int size() {
        return this.size;
    }
    
    public boolean endsWith(final byte[] array) {
        int i = 0;
        int start = this.size - array.length;
        if (start < 0) {
            return false;
        }
        while (start < this.size) {
            if (this.buffer[start++] != array[i++]) {
                return false;
            }
        }
        return true;
    }
}
