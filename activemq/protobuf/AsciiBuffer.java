// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

public final class AsciiBuffer extends Buffer
{
    private int hashCode;
    
    public AsciiBuffer(final Buffer other) {
        super(other);
    }
    
    public AsciiBuffer(final byte[] data, final int offset, final int length) {
        super(data, offset, length);
    }
    
    public AsciiBuffer(final byte[] data) {
        super(data);
    }
    
    public AsciiBuffer(final String input) {
        super(encode(input));
    }
    
    @Override
    public AsciiBuffer compact() {
        if (this.length != this.data.length) {
            return new AsciiBuffer(this.toByteArray());
        }
        return this;
    }
    
    @Override
    public String toString() {
        return decode(this);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == AsciiBuffer.class && this.equals((Buffer)obj));
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = super.hashCode();
        }
        return this.hashCode;
    }
    
    public static byte[] encode(final String value) {
        final int size = value.length();
        final byte[] rc = new byte[size];
        for (int i = 0; i < size; ++i) {
            rc[i] = (byte)(value.charAt(i) & '\u00ff');
        }
        return rc;
    }
    
    public static String decode(final Buffer value) {
        final int size = value.getLength();
        final char[] rc = new char[size];
        for (int i = 0; i < size; ++i) {
            rc[i] = (char)(value.byteAt(i) & 0xFF);
        }
        return new String(rc);
    }
}
