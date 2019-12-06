// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.io.UnsupportedEncodingException;

public final class UTF8Buffer extends Buffer
{
    int hashCode;
    String value;
    
    public UTF8Buffer(final Buffer other) {
        super(other);
    }
    
    public UTF8Buffer(final byte[] data, final int offset, final int length) {
        super(data, offset, length);
    }
    
    public UTF8Buffer(final byte[] data) {
        super(data);
    }
    
    public UTF8Buffer(final String input) {
        super(encode(input));
    }
    
    @Override
    public UTF8Buffer compact() {
        if (this.length != this.data.length) {
            return new UTF8Buffer(this.toByteArray());
        }
        return this;
    }
    
    @Override
    public String toString() {
        if (this.value == null) {
            this.value = decode(this);
        }
        return this.value;
    }
    
    @Override
    public int compareTo(final Buffer other) {
        return this.toString().compareTo(other.toString());
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == UTF8Buffer.class && this.equals((Buffer)obj));
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = super.hashCode();
        }
        return this.hashCode;
    }
    
    public static byte[] encode(final String value) {
        try {
            return value.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("A UnsupportedEncodingException was thrown for teh UTF-8 encoding. (This should never happen)");
        }
    }
    
    public static String decode(final Buffer buffer) {
        try {
            return new String(buffer.getData(), buffer.getOffset(), buffer.getLength(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("A UnsupportedEncodingException was thrown for teh UTF-8 encoding. (This should never happen)");
        }
    }
}
