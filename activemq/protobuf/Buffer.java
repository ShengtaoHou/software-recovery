// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.util.Iterator;
import java.util.List;

public class Buffer implements Comparable<Buffer>
{
    public final byte[] data;
    public final int offset;
    public final int length;
    
    public Buffer(final Buffer other) {
        this(other.data, other.offset, other.length);
    }
    
    public Buffer(final byte[] data) {
        this(data, 0, data.length);
    }
    
    public Buffer(final byte[] data, final int offset, final int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }
    
    @Deprecated
    public Buffer(final String value) {
        this(UTF8Buffer.encode(value));
    }
    
    public final Buffer slice(final int low, final int high) {
        int sz;
        if (high < 0) {
            sz = this.length + high;
        }
        else {
            sz = high - low;
        }
        if (sz < 0) {
            sz = 0;
        }
        return new Buffer(this.data, this.offset + low, sz);
    }
    
    public final byte[] getData() {
        return this.data;
    }
    
    public final int getLength() {
        return this.length;
    }
    
    public final int getOffset() {
        return this.offset;
    }
    
    public Buffer compact() {
        if (this.length != this.data.length) {
            return new Buffer(this.toByteArray());
        }
        return this;
    }
    
    public final byte[] toByteArray() {
        byte[] data = this.data;
        final int length = this.length;
        if (length != data.length) {
            final byte[] t = new byte[length];
            System.arraycopy(data, this.offset, t, 0, length);
            data = t;
        }
        return data;
    }
    
    public byte byteAt(final int i) {
        return this.data[this.offset + i];
    }
    
    @Override
    public int hashCode() {
        final byte[] target = new byte[4];
        for (int i = 0; i < this.length; ++i) {
            final byte[] array = target;
            final int n = i % 4;
            array[n] ^= this.data[this.offset + i];
        }
        return target[0] << 24 | target[1] << 16 | target[2] << 8 | target[3];
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == Buffer.class && this.equals((Buffer)obj));
    }
    
    public final boolean equals(final Buffer obj) {
        if (this.length != obj.length) {
            return false;
        }
        for (int i = 0; i < this.length; ++i) {
            if (obj.data[obj.offset + i] != this.data[this.offset + i]) {
                return false;
            }
        }
        return true;
    }
    
    public final BufferInputStream newInput() {
        return new BufferInputStream(this);
    }
    
    public final BufferOutputStream newOutput() {
        return new BufferOutputStream(this);
    }
    
    public final boolean isEmpty() {
        return this.length == 0;
    }
    
    public final boolean contains(final byte value) {
        return this.indexOf(value, 0) >= 0;
    }
    
    public final int indexOf(final byte value, final int pos) {
        for (int i = pos; i < this.length; ++i) {
            if (this.data[this.offset + i] == value) {
                return i;
            }
        }
        return -1;
    }
    
    public static final Buffer join(final List<Buffer> items, final Buffer seperator) {
        if (items.isEmpty()) {
            return new Buffer(seperator.data, 0, 0);
        }
        int size = 0;
        for (final Buffer item : items) {
            size += item.length;
        }
        size += seperator.length * (items.size() - 1);
        int pos = 0;
        final byte[] data = new byte[size];
        for (final Buffer item2 : items) {
            if (pos != 0) {
                System.arraycopy(seperator.data, seperator.offset, data, pos, seperator.length);
                pos += seperator.length;
            }
            System.arraycopy(item2.data, item2.offset, data, pos, item2.length);
            pos += item2.length;
        }
        return new Buffer(data, 0, size);
    }
    
    @Deprecated
    public String toStringUtf8() {
        return UTF8Buffer.decode(this);
    }
    
    public int compareTo(final Buffer o) {
        int minLength = Math.min(this.length, o.length);
        if (this.offset == o.offset) {
            for (int pos = this.offset, limit = minLength + this.offset; pos < limit; ++pos) {
                final byte b1 = this.data[pos];
                final byte b2 = o.data[pos];
                if (b1 != b2) {
                    return b1 - b2;
                }
            }
        }
        else {
            int offset1 = this.offset;
            int offset2 = o.offset;
            while (minLength-- != 0) {
                final byte b1 = this.data[offset1++];
                final byte b2 = o.data[offset2++];
                if (b1 != b2) {
                    return b1 - b2;
                }
            }
        }
        return this.length - o.length;
    }
}
