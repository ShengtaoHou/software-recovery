// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.UTFDataFormatException;
import java.io.IOException;
import java.io.DataOutput;
import java.io.OutputStream;

public final class DataByteArrayOutputStream extends OutputStream implements DataOutput
{
    private static final int DEFAULT_SIZE = 2048;
    private byte[] buf;
    private int pos;
    
    public DataByteArrayOutputStream(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        this.buf = new byte[size];
    }
    
    public DataByteArrayOutputStream() {
        this(2048);
    }
    
    public void restart(final int size) {
        this.buf = new byte[size];
        this.pos = 0;
    }
    
    public void restart() {
        this.restart(2048);
    }
    
    public ByteSequence toByteSequence() {
        return new ByteSequence(this.buf, 0, this.pos);
    }
    
    @Override
    public void write(final int b) {
        final int newcount = this.pos + 1;
        this.ensureEnoughBuffer(newcount);
        this.buf[this.pos] = (byte)b;
        this.pos = newcount;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        if (len == 0) {
            return;
        }
        final int newcount = this.pos + len;
        this.ensureEnoughBuffer(newcount);
        System.arraycopy(b, off, this.buf, this.pos, len);
        this.pos = newcount;
    }
    
    public byte[] getData() {
        return this.buf;
    }
    
    public void reset() {
        this.pos = 0;
    }
    
    public void position(final int offset) {
        this.ensureEnoughBuffer(offset);
        this.pos = offset;
    }
    
    public int size() {
        return this.pos;
    }
    
    @Override
    public void writeBoolean(final boolean v) {
        this.ensureEnoughBuffer(this.pos + 1);
        this.buf[this.pos++] = (byte)(v ? 1 : 0);
    }
    
    @Override
    public void writeByte(final int v) {
        this.ensureEnoughBuffer(this.pos + 1);
        this.buf[this.pos++] = (byte)(v >>> 0);
    }
    
    @Override
    public void writeShort(final int v) {
        this.ensureEnoughBuffer(this.pos + 2);
        this.buf[this.pos++] = (byte)(v >>> 8);
        this.buf[this.pos++] = (byte)(v >>> 0);
    }
    
    @Override
    public void writeChar(final int v) {
        this.ensureEnoughBuffer(this.pos + 2);
        this.buf[this.pos++] = (byte)(v >>> 8);
        this.buf[this.pos++] = (byte)(v >>> 0);
    }
    
    @Override
    public void writeInt(final int v) {
        this.ensureEnoughBuffer(this.pos + 4);
        this.buf[this.pos++] = (byte)(v >>> 24);
        this.buf[this.pos++] = (byte)(v >>> 16);
        this.buf[this.pos++] = (byte)(v >>> 8);
        this.buf[this.pos++] = (byte)(v >>> 0);
    }
    
    @Override
    public void writeLong(final long v) {
        this.ensureEnoughBuffer(this.pos + 8);
        this.buf[this.pos++] = (byte)(v >>> 56);
        this.buf[this.pos++] = (byte)(v >>> 48);
        this.buf[this.pos++] = (byte)(v >>> 40);
        this.buf[this.pos++] = (byte)(v >>> 32);
        this.buf[this.pos++] = (byte)(v >>> 24);
        this.buf[this.pos++] = (byte)(v >>> 16);
        this.buf[this.pos++] = (byte)(v >>> 8);
        this.buf[this.pos++] = (byte)(v >>> 0);
    }
    
    @Override
    public void writeFloat(final float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }
    
    @Override
    public void writeDouble(final double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }
    
    @Override
    public void writeBytes(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            this.write((byte)s.charAt(i));
        }
    }
    
    @Override
    public void writeChars(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final int c = s.charAt(i);
            this.write(c >>> 8 & 0xFF);
            this.write(c >>> 0 & 0xFF);
        }
    }
    
    @Override
    public void writeUTF(final String str) throws IOException {
        final int strlen = str.length();
        int encodedsize = 0;
        for (int i = 0; i < strlen; ++i) {
            final int c = str.charAt(i);
            if (c >= 1 && c <= 127) {
                ++encodedsize;
            }
            else if (c > 2047) {
                encodedsize += 3;
            }
            else {
                encodedsize += 2;
            }
        }
        if (encodedsize > 65535) {
            throw new UTFDataFormatException("encoded string too long: " + encodedsize + " bytes");
        }
        this.ensureEnoughBuffer(this.pos + encodedsize + 2);
        this.writeShort(encodedsize);
        int i;
        int c;
        for (i = 0, i = 0; i < strlen; ++i) {
            c = str.charAt(i);
            if (c < 1) {
                break;
            }
            if (c > 127) {
                break;
            }
            this.buf[this.pos++] = (byte)c;
        }
        while (i < strlen) {
            c = str.charAt(i);
            if (c >= 1 && c <= 127) {
                this.buf[this.pos++] = (byte)c;
            }
            else if (c > 2047) {
                this.buf[this.pos++] = (byte)(0xE0 | (c >> 12 & 0xF));
                this.buf[this.pos++] = (byte)(0x80 | (c >> 6 & 0x3F));
                this.buf[this.pos++] = (byte)(0x80 | (c >> 0 & 0x3F));
            }
            else {
                this.buf[this.pos++] = (byte)(0xC0 | (c >> 6 & 0x1F));
                this.buf[this.pos++] = (byte)(0x80 | (c >> 0 & 0x3F));
            }
            ++i;
        }
    }
    
    private void ensureEnoughBuffer(final int newcount) {
        if (newcount > this.buf.length) {
            final byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.pos);
            this.buf = newbuf;
        }
    }
}
