// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.util;

import java.io.UTFDataFormatException;
import java.io.IOException;
import org.apache.activemq.util.ByteSequence;
import java.io.DataInput;
import java.io.InputStream;

public final class DataByteArrayInputStream extends InputStream implements DataInput
{
    private byte[] buf;
    private int pos;
    private int offset;
    private int length;
    private byte[] work;
    
    public DataByteArrayInputStream(final byte[] buf) {
        this.buf = buf;
        this.pos = 0;
        this.offset = 0;
        this.length = buf.length;
        this.work = new byte[8];
    }
    
    public DataByteArrayInputStream(final ByteSequence sequence) {
        this.buf = sequence.getData();
        this.offset = sequence.getOffset();
        this.pos = this.offset;
        this.length = sequence.length;
        this.work = new byte[8];
    }
    
    public DataByteArrayInputStream() {
        this(new byte[0]);
    }
    
    public int size() {
        return this.pos - this.offset;
    }
    
    public byte[] getRawData() {
        return this.buf;
    }
    
    public void restart(final byte[] newBuff) {
        this.buf = newBuff;
        this.pos = 0;
        this.length = newBuff.length;
    }
    
    public void restart() {
        this.pos = 0;
        this.length = this.buf.length;
    }
    
    public void restart(final ByteSequence sequence) {
        this.buf = sequence.getData();
        this.pos = sequence.getOffset();
        this.length = sequence.getLength();
    }
    
    public void restart(final int size) {
        if (this.buf == null || this.buf.length < size) {
            this.buf = new byte[size];
        }
        this.restart(this.buf);
        this.length = size;
    }
    
    @Override
    public int read() {
        return (this.pos < this.length) ? (this.buf[this.pos++] & 0xFF) : -1;
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (this.pos >= this.length) {
            return -1;
        }
        if (this.pos + len > this.length) {
            len = this.length - this.pos;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(this.buf, this.pos, b, off, len);
        this.pos += len;
        return len;
    }
    
    @Override
    public int available() {
        return this.length - this.pos;
    }
    
    @Override
    public void readFully(final byte[] b) {
        this.read(b, 0, b.length);
    }
    
    @Override
    public void readFully(final byte[] b, final int off, final int len) {
        this.read(b, off, len);
    }
    
    @Override
    public int skipBytes(int n) {
        if (this.pos + n > this.length) {
            n = this.length - this.pos;
        }
        if (n < 0) {
            return 0;
        }
        this.pos += n;
        return n;
    }
    
    @Override
    public boolean readBoolean() {
        return this.read() != 0;
    }
    
    @Override
    public byte readByte() {
        return (byte)this.read();
    }
    
    @Override
    public int readUnsignedByte() {
        return this.read();
    }
    
    @Override
    public short readShort() {
        this.read(this.work, 0, 2);
        return (short)((this.work[0] & 0xFF) << 8 | (this.work[1] & 0xFF));
    }
    
    @Override
    public int readUnsignedShort() {
        this.read(this.work, 0, 2);
        return (this.work[0] & 0xFF) << 8 | (this.work[1] & 0xFF);
    }
    
    @Override
    public char readChar() {
        this.read(this.work, 0, 2);
        return (char)((this.work[0] & 0xFF) << 8 | (this.work[1] & 0xFF));
    }
    
    @Override
    public int readInt() {
        this.read(this.work, 0, 4);
        return (this.work[0] & 0xFF) << 24 | (this.work[1] & 0xFF) << 16 | (this.work[2] & 0xFF) << 8 | (this.work[3] & 0xFF);
    }
    
    @Override
    public long readLong() {
        this.read(this.work, 0, 8);
        final int i1 = (this.work[0] & 0xFF) << 24 | (this.work[1] & 0xFF) << 16 | (this.work[2] & 0xFF) << 8 | (this.work[3] & 0xFF);
        final int i2 = (this.work[4] & 0xFF) << 24 | (this.work[5] & 0xFF) << 16 | (this.work[6] & 0xFF) << 8 | (this.work[7] & 0xFF);
        return ((long)i1 & 0xFFFFFFFFL) << 32 | ((long)i2 & 0xFFFFFFFFL);
    }
    
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }
    
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }
    
    @Override
    public String readLine() {
        final int start = this.pos;
        while (this.pos < this.length) {
            int c = this.read();
            if (c == 10) {
                break;
            }
            if (c != 13) {
                continue;
            }
            c = this.read();
            if (c != 10 && c != -1) {
                --this.pos;
                break;
            }
            break;
        }
        return new String(this.buf, start, this.pos);
    }
    
    @Override
    public String readUTF() throws IOException {
        final int length = this.readUnsignedShort();
        final int endPos = this.pos + length;
        int count = 0;
        final char[] characters = new char[length];
        while (this.pos < endPos) {
            if ((characters[count] = (char)this.buf[this.pos++]) < '\u0080') {
                ++count;
            }
            else {
                final int a;
                if (((a = characters[count]) & 0xE0) == 0xC0) {
                    if (this.pos >= endPos) {
                        throw new UTFDataFormatException("bad string");
                    }
                    final int b = this.buf[this.pos++];
                    if ((b & 0xC0) != 0x80) {
                        throw new UTFDataFormatException("bad string");
                    }
                    characters[count++] = (char)((a & 0x1F) << 6 | (b & 0x3F));
                }
                else {
                    if ((a & 0xF0) != 0xE0) {
                        throw new UTFDataFormatException("bad string");
                    }
                    if (this.pos + 1 >= endPos) {
                        throw new UTFDataFormatException("bad string");
                    }
                    final int b = this.buf[this.pos++];
                    final int c = this.buf[this.pos++];
                    if ((b & 0xC0) != 0x80 || (c & 0xC0) != 0x80) {
                        throw new UTFDataFormatException("bad string");
                    }
                    characters[count++] = (char)((a & 0xF) << 12 | (b & 0x3F) << 6 | (c & 0x3F));
                }
            }
        }
        return new String(characters, 0, count);
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public void setPos(final int pos) {
        this.pos = pos;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void setLength(final int length) {
        this.length = length;
    }
}
