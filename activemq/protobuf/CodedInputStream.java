// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.FilterInputStream;

public final class CodedInputStream extends FilterInputStream
{
    private int lastTag;
    private int limit;
    private int pos;
    private BufferInputStream bis;
    
    public CodedInputStream(final InputStream in) {
        super(in);
        this.lastTag = 0;
        this.limit = Integer.MAX_VALUE;
        if (in.getClass() == BufferInputStream.class) {
            this.bis = (BufferInputStream)in;
        }
    }
    
    public CodedInputStream(final Buffer data) {
        this(new BufferInputStream(data));
        this.limit = data.length;
    }
    
    public CodedInputStream(final byte[] data) {
        this(new BufferInputStream(data));
        this.limit = data.length;
    }
    
    public int readTag() throws IOException {
        if (this.pos >= this.limit) {
            return this.lastTag = 0;
        }
        try {
            this.lastTag = this.readRawVarint32();
            if (this.lastTag == 0) {
                throw InvalidProtocolBufferException.invalidTag();
            }
            return this.lastTag;
        }
        catch (EOFException e) {
            return this.lastTag = 0;
        }
    }
    
    public void checkLastTagWas(final int value) throws InvalidProtocolBufferException {
        if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
        }
    }
    
    public boolean skipField(final int tag) throws IOException {
        switch (WireFormat.getTagWireType(tag)) {
            case 0: {
                this.readInt32();
                return true;
            }
            case 1: {
                this.readRawLittleEndian64();
                return true;
            }
            case 2: {
                this.skipRawBytes(this.readRawVarint32());
                return true;
            }
            case 3: {
                this.skipMessage();
                this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                return true;
            }
            case 4: {
                return false;
            }
            case 5: {
                this.readRawLittleEndian32();
                return true;
            }
            default: {
                throw InvalidProtocolBufferException.invalidWireType();
            }
        }
    }
    
    public void skipMessage() throws IOException {
        int tag;
        do {
            tag = this.readTag();
        } while (tag != 0 && this.skipField(tag));
    }
    
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readRawLittleEndian64());
    }
    
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readRawLittleEndian32());
    }
    
    public long readUInt64() throws IOException {
        return this.readRawVarint64();
    }
    
    public long readInt64() throws IOException {
        return this.readRawVarint64();
    }
    
    public int readInt32() throws IOException {
        return this.readRawVarint32();
    }
    
    public long readFixed64() throws IOException {
        return this.readRawLittleEndian64();
    }
    
    public int readFixed32() throws IOException {
        return this.readRawLittleEndian32();
    }
    
    public boolean readBool() throws IOException {
        return this.readRawVarint32() != 0;
    }
    
    public String readString() throws IOException {
        final int size = this.readRawVarint32();
        final Buffer data = this.readRawBytes(size);
        return new String(data.data, data.offset, data.length, "UTF-8");
    }
    
    public Buffer readBytes() throws IOException {
        final int size = this.readRawVarint32();
        return this.readRawBytes(size);
    }
    
    public int readUInt32() throws IOException {
        return this.readRawVarint32();
    }
    
    public int readEnum() throws IOException {
        return this.readRawVarint32();
    }
    
    public int readSFixed32() throws IOException {
        return this.readRawLittleEndian32();
    }
    
    public long readSFixed64() throws IOException {
        return this.readRawLittleEndian64();
    }
    
    public int readSInt32() throws IOException {
        return decodeZigZag32(this.readRawVarint32());
    }
    
    public long readSInt64() throws IOException {
        return decodeZigZag64(this.readRawVarint64());
    }
    
    public int readRawVarint32() throws IOException {
        byte tmp = this.readRawByte();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7F;
        if ((tmp = this.readRawByte()) >= 0) {
            result |= tmp << 7;
        }
        else {
            result |= (tmp & 0x7F) << 7;
            if ((tmp = this.readRawByte()) >= 0) {
                result |= tmp << 14;
            }
            else {
                result |= (tmp & 0x7F) << 14;
                if ((tmp = this.readRawByte()) >= 0) {
                    result |= tmp << 21;
                }
                else {
                    result |= (tmp & 0x7F) << 21;
                    result |= (tmp = this.readRawByte()) << 28;
                    if (tmp < 0) {
                        for (int i = 0; i < 5; ++i) {
                            if (this.readRawByte() >= 0) {
                                return result;
                            }
                        }
                        throw InvalidProtocolBufferException.malformedVarint();
                    }
                }
            }
        }
        return result;
    }
    
    public long readRawVarint64() throws IOException {
        int shift = 0;
        long result = 0L;
        while (shift < 64) {
            final byte b = this.readRawByte();
            result |= (long)(b & 0x7F) << shift;
            if ((b & 0x80) == 0x0) {
                return result;
            }
            shift += 7;
        }
        throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public int readRawLittleEndian32() throws IOException {
        final byte b1 = this.readRawByte();
        final byte b2 = this.readRawByte();
        final byte b3 = this.readRawByte();
        final byte b4 = this.readRawByte();
        return (b1 & 0xFF) | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24;
    }
    
    public long readRawLittleEndian64() throws IOException {
        final byte b1 = this.readRawByte();
        final byte b2 = this.readRawByte();
        final byte b3 = this.readRawByte();
        final byte b4 = this.readRawByte();
        final byte b5 = this.readRawByte();
        final byte b6 = this.readRawByte();
        final byte b7 = this.readRawByte();
        final byte b8 = this.readRawByte();
        return ((long)b1 & 0xFFL) | ((long)b2 & 0xFFL) << 8 | ((long)b3 & 0xFFL) << 16 | ((long)b4 & 0xFFL) << 24 | ((long)b5 & 0xFFL) << 32 | ((long)b6 & 0xFFL) << 40 | ((long)b7 & 0xFFL) << 48 | ((long)b8 & 0xFFL) << 56;
    }
    
    public static int decodeZigZag32(final int n) {
        return n >>> 1 ^ -(n & 0x1);
    }
    
    public static long decodeZigZag64(final long n) {
        return n >>> 1 ^ -(n & 0x1L);
    }
    
    public byte readRawByte() throws IOException {
        if (this.pos >= this.limit) {
            throw new EOFException();
        }
        final int rc = this.in.read();
        if (rc < 0) {
            throw new EOFException();
        }
        ++this.pos;
        return (byte)(rc & 0xFF);
    }
    
    public Buffer readRawBytes(final int size) throws IOException {
        if (size == 0) {
            return new Buffer(new byte[0]);
        }
        if (this.pos + size > this.limit) {
            throw new EOFException();
        }
        if (this.bis == null) {
            final byte[] rc = new byte[size];
            int c;
            for (int pos = 0; pos < size; pos += c) {
                c = this.in.read(rc, pos, size - pos);
                if (c < 0) {
                    throw new EOFException();
                }
                this.pos += c;
            }
            return new Buffer(rc);
        }
        final Buffer rc2 = this.bis.readBuffer(size);
        if (rc2 == null || rc2.getLength() < size) {
            throw new EOFException();
        }
        this.pos += rc2.getLength();
        return rc2;
    }
    
    public void skipRawBytes(final int size) throws IOException {
        int n;
        for (int pos = 0; pos < size; pos += n) {
            n = (int)this.in.skip(size - pos);
        }
    }
    
    public int pushLimit(final int limit) {
        final int rc = this.limit;
        this.limit = this.pos + limit;
        return rc;
    }
    
    public void popLimit(final int limit) {
        this.limit = limit;
    }
}
