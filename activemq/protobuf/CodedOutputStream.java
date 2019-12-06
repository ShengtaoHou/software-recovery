// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public final class CodedOutputStream extends FilterOutputStream
{
    private BufferOutputStream bos;
    public static final int LITTLE_ENDIAN_32_SIZE = 4;
    public static final int LITTLE_ENDIAN_64_SIZE = 8;
    
    public CodedOutputStream(final OutputStream os) {
        super(os);
        if (os instanceof BufferOutputStream) {
            this.bos = (BufferOutputStream)os;
        }
    }
    
    public CodedOutputStream(final byte[] data) {
        super(new BufferOutputStream(data));
    }
    
    public CodedOutputStream(final Buffer data) {
        super(new BufferOutputStream(data));
    }
    
    public void writeDouble(final int fieldNumber, final double value) throws IOException {
        this.writeTag(fieldNumber, 1);
        this.writeRawLittleEndian64(Double.doubleToRawLongBits(value));
    }
    
    public void writeFloat(final int fieldNumber, final float value) throws IOException {
        this.writeTag(fieldNumber, 5);
        this.writeRawLittleEndian32(Float.floatToRawIntBits(value));
    }
    
    public void writeUInt64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeRawVarint64(value);
    }
    
    public void writeInt64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeRawVarint64(value);
    }
    
    public void writeInt32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 0);
        if (value >= 0) {
            this.writeRawVarint32(value);
        }
        else {
            this.writeRawVarint64(value);
        }
    }
    
    public void writeFixed64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 1);
        this.writeRawLittleEndian64(value);
    }
    
    public void writeFixed32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 5);
        this.writeRawLittleEndian32(value);
    }
    
    public void writeBool(final int fieldNumber, final boolean value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeRawByte(value ? 1 : 0);
    }
    
    public void writeString(final int fieldNumber, final String value) throws IOException {
        this.writeTag(fieldNumber, 2);
        final byte[] bytes = value.getBytes("UTF-8");
        this.writeRawVarint32(bytes.length);
        this.writeRawBytes(bytes);
    }
    
    public void writeBytes(final int fieldNumber, final Buffer value) throws IOException {
        this.writeTag(fieldNumber, 2);
        this.writeRawVarint32(value.length);
        this.writeRawBytes(value.data, value.offset, value.length);
    }
    
    public void writeUInt32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeRawVarint32(value);
    }
    
    public void writeEnum(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeRawVarint32(value);
    }
    
    public void writeSFixed32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 5);
        this.writeRawLittleEndian32(value);
    }
    
    public void writeSFixed64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 1);
        this.writeRawLittleEndian64(value);
    }
    
    public void writeSInt32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeRawVarint32(encodeZigZag32(value));
    }
    
    public void writeSInt64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeRawVarint64(encodeZigZag64(value));
    }
    
    public static int computeDoubleSize(final int fieldNumber, final double value) {
        return computeTagSize(fieldNumber) + 8;
    }
    
    public static int computeFloatSize(final int fieldNumber, final float value) {
        return computeTagSize(fieldNumber) + 4;
    }
    
    public static int computeUInt64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + computeRawVarint64Size(value);
    }
    
    public static int computeInt64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + computeRawVarint64Size(value);
    }
    
    public static int computeInt32Size(final int fieldNumber, final int value) {
        if (value >= 0) {
            return computeTagSize(fieldNumber) + computeRawVarint32Size(value);
        }
        return computeTagSize(fieldNumber) + 10;
    }
    
    public static int computeFixed64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + 8;
    }
    
    public static int computeFixed32Size(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + 4;
    }
    
    public static int computeBoolSize(final int fieldNumber, final boolean value) {
        return computeTagSize(fieldNumber) + 1;
    }
    
    public static int computeStringSize(final int fieldNumber, final String value) {
        try {
            final byte[] bytes = value.getBytes("UTF-8");
            return computeTagSize(fieldNumber) + computeRawVarint32Size(bytes.length) + bytes.length;
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported.", e);
        }
    }
    
    public static int computeBytesSize(final int fieldNumber, final Buffer value) {
        return computeTagSize(fieldNumber) + computeRawVarint32Size(value.length) + value.length;
    }
    
    public static int computeUInt32Size(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + computeRawVarint32Size(value);
    }
    
    public static int computeEnumSize(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + computeRawVarint32Size(value);
    }
    
    public static int computeSFixed32Size(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + 4;
    }
    
    public static int computeSFixed64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + 8;
    }
    
    public static int computeSInt32Size(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + computeRawVarint32Size(encodeZigZag32(value));
    }
    
    public static int computeSInt64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + computeRawVarint64Size(encodeZigZag64(value));
    }
    
    public void writeRawByte(final byte value) throws IOException {
        this.out.write(value);
    }
    
    public void writeRawByte(final int value) throws IOException {
        this.writeRawByte((byte)value);
    }
    
    public void writeRawBytes(final byte[] value) throws IOException {
        this.writeRawBytes(value, 0, value.length);
    }
    
    public void writeRawBytes(final byte[] value, final int offset, final int length) throws IOException {
        this.out.write(value, offset, length);
    }
    
    public void writeRawBytes(final Buffer data) throws IOException {
        this.out.write(data.data, data.offset, data.length);
    }
    
    public void writeTag(final int fieldNumber, final int wireType) throws IOException {
        this.writeRawVarint32(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    public static int computeTagSize(final int fieldNumber) {
        return computeRawVarint32Size(WireFormat.makeTag(fieldNumber, 0));
    }
    
    public void writeRawVarint32(int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0x0) {
            this.writeRawByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        this.writeRawByte(value);
    }
    
    public static int computeRawVarint32Size(final int value) {
        if ((value & 0xFFFFFF80) == 0x0) {
            return 1;
        }
        if ((value & 0xFFFFC000) == 0x0) {
            return 2;
        }
        if ((value & 0xFFE00000) == 0x0) {
            return 3;
        }
        if ((value & 0xF0000000) == 0x0) {
            return 4;
        }
        return 5;
    }
    
    public void writeRawVarint64(long value) throws IOException {
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0x0L) {
            this.writeRawByte(((int)value & 0x7F) | 0x80);
            value >>>= 7;
        }
        this.writeRawByte((int)value);
    }
    
    public static int computeRawVarint64Size(final long value) {
        if ((value & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            return 1;
        }
        if ((value & 0xFFFFFFFFFFFFC000L) == 0x0L) {
            return 2;
        }
        if ((value & 0xFFFFFFFFFFE00000L) == 0x0L) {
            return 3;
        }
        if ((value & 0xFFFFFFFFF0000000L) == 0x0L) {
            return 4;
        }
        if ((value & 0xFFFFFFF800000000L) == 0x0L) {
            return 5;
        }
        if ((value & 0xFFFFFC0000000000L) == 0x0L) {
            return 6;
        }
        if ((value & 0xFFFE000000000000L) == 0x0L) {
            return 7;
        }
        if ((value & 0xFF00000000000000L) == 0x0L) {
            return 8;
        }
        if ((value & Long.MIN_VALUE) == 0x0L) {
            return 9;
        }
        return 10;
    }
    
    public void writeRawLittleEndian32(final int value) throws IOException {
        this.writeRawByte(value & 0xFF);
        this.writeRawByte(value >> 8 & 0xFF);
        this.writeRawByte(value >> 16 & 0xFF);
        this.writeRawByte(value >> 24 & 0xFF);
    }
    
    public void writeRawLittleEndian64(final long value) throws IOException {
        this.writeRawByte((int)value & 0xFF);
        this.writeRawByte((int)(value >> 8) & 0xFF);
        this.writeRawByte((int)(value >> 16) & 0xFF);
        this.writeRawByte((int)(value >> 24) & 0xFF);
        this.writeRawByte((int)(value >> 32) & 0xFF);
        this.writeRawByte((int)(value >> 40) & 0xFF);
        this.writeRawByte((int)(value >> 48) & 0xFF);
        this.writeRawByte((int)(value >> 56) & 0xFF);
    }
    
    public static int encodeZigZag32(final int n) {
        return n << 1 ^ n >> 31;
    }
    
    public static long encodeZigZag64(final long n) {
        return n << 1 ^ n >> 63;
    }
    
    public void checkNoSpaceLeft() {
    }
    
    public Buffer getNextBuffer(final int size) throws IOException {
        if (this.bos == null) {
            return null;
        }
        return this.bos.getNextBuffer(size);
    }
}
