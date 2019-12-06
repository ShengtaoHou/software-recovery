// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import org.apache.activemq.store.kahadb.disk.page.PageFile;
import java.io.UTFDataFormatException;
import java.io.IOException;
import org.apache.activemq.util.ByteSequence;
import java.io.DataOutput;
import java.io.OutputStream;

public class DataByteArrayOutputStream extends OutputStream implements DataOutput
{
    private static final int DEFAULT_SIZE;
    protected byte[] buf;
    protected int pos;
    
    public DataByteArrayOutputStream(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        this.buf = new byte[size];
    }
    
    public DataByteArrayOutputStream() {
        this(DataByteArrayOutputStream.DEFAULT_SIZE);
    }
    
    public void restart(final int size) {
        this.buf = new byte[size];
        this.pos = 0;
    }
    
    public void restart() {
        this.restart(DataByteArrayOutputStream.DEFAULT_SIZE);
    }
    
    public ByteSequence toByteSequence() {
        return new ByteSequence(this.buf, 0, this.pos);
    }
    
    @Override
    public void write(final int b) throws IOException {
        final int newcount = this.pos + 1;
        this.ensureEnoughBuffer(newcount);
        this.buf[this.pos] = (byte)b;
        this.pos = newcount;
        this.onWrite();
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return;
        }
        final int newcount = this.pos + len;
        this.ensureEnoughBuffer(newcount);
        System.arraycopy(b, off, this.buf, this.pos, len);
        this.pos = newcount;
        this.onWrite();
    }
    
    public byte[] getData() {
        return this.buf;
    }
    
    public void reset() {
        this.pos = 0;
    }
    
    public void position(final int offset) throws IOException {
        this.ensureEnoughBuffer(offset);
        this.pos = offset;
        this.onWrite();
    }
    
    public int size() {
        return this.pos;
    }
    
    @Override
    public void writeBoolean(final boolean v) throws IOException {
        this.ensureEnoughBuffer(this.pos + 1);
        this.buf[this.pos++] = (byte)(v ? 1 : 0);
        this.onWrite();
    }
    
    @Override
    public void writeByte(final int v) throws IOException {
        this.ensureEnoughBuffer(this.pos + 1);
        this.buf[this.pos++] = (byte)(v >>> 0);
        this.onWrite();
    }
    
    @Override
    public void writeShort(final int v) throws IOException {
        this.ensureEnoughBuffer(this.pos + 2);
        this.buf[this.pos++] = (byte)(v >>> 8);
        this.buf[this.pos++] = (byte)(v >>> 0);
        this.onWrite();
    }
    
    @Override
    public void writeChar(final int v) throws IOException {
        this.ensureEnoughBuffer(this.pos + 2);
        this.buf[this.pos++] = (byte)(v >>> 8);
        this.buf[this.pos++] = (byte)(v >>> 0);
        this.onWrite();
    }
    
    @Override
    public void writeInt(final int v) throws IOException {
        this.ensureEnoughBuffer(this.pos + 4);
        this.buf[this.pos++] = (byte)(v >>> 24);
        this.buf[this.pos++] = (byte)(v >>> 16);
        this.buf[this.pos++] = (byte)(v >>> 8);
        this.buf[this.pos++] = (byte)(v >>> 0);
        this.onWrite();
    }
    
    @Override
    public void writeLong(final long v) throws IOException {
        this.ensureEnoughBuffer(this.pos + 8);
        this.buf[this.pos++] = (byte)(v >>> 56);
        this.buf[this.pos++] = (byte)(v >>> 48);
        this.buf[this.pos++] = (byte)(v >>> 40);
        this.buf[this.pos++] = (byte)(v >>> 32);
        this.buf[this.pos++] = (byte)(v >>> 24);
        this.buf[this.pos++] = (byte)(v >>> 16);
        this.buf[this.pos++] = (byte)(v >>> 8);
        this.buf[this.pos++] = (byte)(v >>> 0);
        this.onWrite();
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
    public void writeBytes(final String s) throws IOException {
        for (int length = s.length(), i = 0; i < length; ++i) {
            this.write((byte)s.charAt(i));
        }
    }
    
    @Override
    public void writeChars(final String s) throws IOException {
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
        for (int i = 0; i < strlen; ++i) {
            final int charValue = str.charAt(i);
            if (charValue > 0 && charValue <= 127) {
                this.buf[this.pos++] = (byte)charValue;
            }
            else if (charValue <= 2047) {
                this.buf[this.pos++] = (byte)(0xC0 | (0x1F & charValue >> 6));
                this.buf[this.pos++] = (byte)(0x80 | (0x3F & charValue));
            }
            else {
                this.buf[this.pos++] = (byte)(0xE0 | (0xF & charValue >> 12));
                this.buf[this.pos++] = (byte)(0x80 | (0x3F & charValue >> 6));
                this.buf[this.pos++] = (byte)(0x80 | (0x3F & charValue));
            }
        }
        this.onWrite();
    }
    
    private void ensureEnoughBuffer(final int newcount) {
        if (newcount > this.buf.length) {
            final byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.pos);
            this.buf = newbuf;
        }
    }
    
    protected void onWrite() throws IOException {
    }
    
    public void skip(final int size) throws IOException {
        this.ensureEnoughBuffer(this.pos + size);
        this.pos += size;
        this.onWrite();
    }
    
    public ByteSequence getByteSequence() {
        return new ByteSequence(this.buf, 0, this.pos);
    }
    
    static {
        DEFAULT_SIZE = PageFile.DEFAULT_PAGE_SIZE;
    }
}
