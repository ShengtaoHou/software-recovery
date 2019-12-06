// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

public class ByteSequence
{
    public byte[] data;
    public int offset;
    public int length;
    
    public ByteSequence() {
    }
    
    public ByteSequence(final byte[] data) {
        this.data = data;
        this.offset = 0;
        this.length = data.length;
    }
    
    public ByteSequence(final byte[] data, final int offset, final int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public void setData(final byte[] data) {
        this.data = data;
    }
    
    public void setLength(final int length) {
        this.length = length;
    }
    
    public void setOffset(final int offset) {
        this.offset = offset;
    }
    
    public void compact() {
        if (this.length != this.data.length) {
            final byte[] t = new byte[this.length];
            System.arraycopy(this.data, this.offset, t, 0, this.length);
            this.data = t;
            this.offset = 0;
        }
    }
    
    public int indexOf(final ByteSequence needle, final int pos) {
        for (int max = this.length - needle.length, i = pos; i < max; ++i) {
            if (this.matches(needle, i)) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean matches(final ByteSequence needle, final int pos) {
        for (int i = 0; i < needle.length; ++i) {
            if (this.data[this.offset + pos + i] != needle.data[needle.offset + i]) {
                return false;
            }
        }
        return true;
    }
    
    private byte getByte(final int i) {
        return this.data[this.offset + i];
    }
    
    public final int indexOf(final byte value, final int pos) {
        for (int i = pos; i < this.length; ++i) {
            if (this.data[this.offset + i] == value) {
                return i;
            }
        }
        return -1;
    }
}
