// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.UTFDataFormatException;
import java.io.IOException;
import java.io.EOFException;
import java.io.DataInput;
import java.io.InputStream;

public final class DataByteArrayInputStream extends InputStream implements DataInput
{
    private byte[] buf;
    private int pos;
    private int offset;
    
    public DataByteArrayInputStream(final byte[] buf) {
        this.buf = buf;
        this.pos = 0;
        this.offset = 0;
    }
    
    public DataByteArrayInputStream(final ByteSequence sequence) {
        this.buf = sequence.getData();
        this.offset = sequence.getOffset();
        this.pos = this.offset;
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
    }
    
    public void restart(final ByteSequence sequence) {
        this.buf = sequence.getData();
        this.pos = sequence.getOffset();
    }
    
    public void restart(final int size) {
        if (this.buf == null || this.buf.length < size) {
            this.buf = new byte[size];
        }
        this.restart(this.buf);
    }
    
    @Override
    public int read() {
        return (this.pos < this.buf.length) ? (this.buf[this.pos++] & 0xFF) : -1;
    }
    
    public int readOrIOException() throws IOException {
        final int rc = this.read();
        if (rc == -1) {
            throw new EOFException();
        }
        return rc;
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (this.pos >= this.buf.length) {
            return -1;
        }
        if (this.pos + len > this.buf.length) {
            len = this.buf.length - this.pos;
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
        return this.buf.length - this.pos;
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
        if (this.pos + n > this.buf.length) {
            n = this.buf.length - this.pos;
        }
        if (n < 0) {
            return 0;
        }
        this.pos += n;
        return n;
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        return this.readOrIOException() != 0;
    }
    
    @Override
    public byte readByte() throws IOException {
        return (byte)this.readOrIOException();
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        return this.readOrIOException();
    }
    
    @Override
    public short readShort() throws IOException {
        final int ch1 = this.readOrIOException();
        final int ch2 = this.readOrIOException();
        return (short)((ch1 << 8) + (ch2 << 0));
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        final int ch1 = this.readOrIOException();
        final int ch2 = this.readOrIOException();
        return (ch1 << 8) + (ch2 << 0);
    }
    
    @Override
    public char readChar() throws IOException {
        final int ch1 = this.readOrIOException();
        final int ch2 = this.readOrIOException();
        return (char)((ch1 << 8) + (ch2 << 0));
    }
    
    @Override
    public int readInt() throws IOException {
        final int ch1 = this.readOrIOException();
        final int ch2 = this.readOrIOException();
        final int ch3 = this.readOrIOException();
        final int ch4 = this.readOrIOException();
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }
    
    @Override
    public long readLong() throws IOException {
        if (this.pos >= this.buf.length) {
            throw new EOFException();
        }
        final long rc = ((long)this.buf[this.pos++] << 56) + ((long)(this.buf[this.pos++] & 0xFF) << 48) + ((long)(this.buf[this.pos++] & 0xFF) << 40) + ((long)(this.buf[this.pos++] & 0xFF) << 32);
        return rc + ((long)(this.buf[this.pos++] & 0xFF) << 24) + ((this.buf[this.pos++] & 0xFF) << 16) + ((this.buf[this.pos++] & 0xFF) << 8) + ((this.buf[this.pos++] & 0xFF) << 0);
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
        while (this.pos < this.buf.length) {
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
        final char[] characters = new char[length];
        int count = 0;
        final int total = this.pos + length;
        while (this.pos < total) {
            final int c = this.buf[this.pos] & 0xFF;
            if (c > 127) {
                break;
            }
            ++this.pos;
            characters[count++] = (char)c;
        }
        while (this.pos < total) {
            final int c = this.buf[this.pos] & 0xFF;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    ++this.pos;
                    characters[count++] = (char)c;
                    continue;
                }
                case 12:
                case 13: {
                    this.pos += 2;
                    if (this.pos > total) {
                        throw new UTFDataFormatException("bad string");
                    }
                    final int c2 = this.buf[this.pos - 1];
                    if ((c2 & 0xC0) != 0x80) {
                        throw new UTFDataFormatException("bad string");
                    }
                    characters[count++] = (char)((c & 0x1F) << 6 | (c2 & 0x3F));
                    continue;
                }
                case 14: {
                    this.pos += 3;
                    if (this.pos > total) {
                        throw new UTFDataFormatException("bad string");
                    }
                    final int c2 = this.buf[this.pos - 2];
                    final int c3 = this.buf[this.pos - 1];
                    if ((c2 & 0xC0) != 0x80 || (c3 & 0xC0) != 0x80) {
                        throw new UTFDataFormatException("bad string");
                    }
                    characters[count++] = (char)((c & 0xF) << 12 | (c2 & 0x3F) << 6 | (c3 & 0x3F) << 0);
                    continue;
                }
                default: {
                    throw new UTFDataFormatException("bad string");
                }
            }
        }
        return new String(characters, 0, count);
    }
}
