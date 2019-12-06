// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.IOException;

public final class ByteSequenceData
{
    private ByteSequenceData() {
    }
    
    public static byte[] toByteArray(final ByteSequence packet) {
        if (packet.offset == 0 && packet.length == packet.data.length) {
            return packet.data;
        }
        final byte[] rc = new byte[packet.length];
        System.arraycopy(packet.data, packet.offset, rc, 0, packet.length);
        return rc;
    }
    
    private static void spaceNeeded(final ByteSequence packet, final int i) {
        assert packet.offset + i <= packet.length;
    }
    
    public static int remaining(final ByteSequence packet) {
        return packet.length - packet.offset;
    }
    
    public static int read(final ByteSequence packet) {
        return packet.data[packet.offset++] & 0xFF;
    }
    
    public static void readFully(final ByteSequence packet, final byte[] b) throws IOException {
        readFully(packet, b, 0, b.length);
    }
    
    public static void readFully(final ByteSequence packet, final byte[] b, final int off, final int len) throws IOException {
        spaceNeeded(packet, len);
        System.arraycopy(packet.data, packet.offset, b, off, len);
        packet.offset += len;
    }
    
    public static int skipBytes(final ByteSequence packet, final int n) throws IOException {
        final int rc = Math.min(n, remaining(packet));
        packet.offset += rc;
        return rc;
    }
    
    public static boolean readBoolean(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 1);
        return read(packet) != 0;
    }
    
    public static byte readByte(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 1);
        return (byte)read(packet);
    }
    
    public static int readUnsignedByte(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 1);
        return read(packet);
    }
    
    public static short readShortBig(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 2);
        return (short)((read(packet) << 8) + (read(packet) << 0));
    }
    
    public static short readShortLittle(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 2);
        return (short)((read(packet) << 0) + (read(packet) << 8));
    }
    
    public static int readUnsignedShortBig(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 2);
        return (read(packet) << 8) + (read(packet) << 0);
    }
    
    public static int readUnsignedShortLittle(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 2);
        return (read(packet) << 0) + (read(packet) << 8);
    }
    
    public static char readCharBig(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 2);
        return (char)((read(packet) << 8) + (read(packet) << 0));
    }
    
    public static char readCharLittle(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 2);
        return (char)((read(packet) << 0) + (read(packet) << 8));
    }
    
    public static int readIntBig(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 4);
        return (read(packet) << 24) + (read(packet) << 16) + (read(packet) << 8) + (read(packet) << 0);
    }
    
    public static int readIntLittle(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 4);
        return (read(packet) << 0) + (read(packet) << 8) + (read(packet) << 16) + (read(packet) << 24);
    }
    
    public static long readLongBig(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 8);
        return ((long)read(packet) << 56) + ((long)read(packet) << 48) + ((long)read(packet) << 40) + ((long)read(packet) << 32) + ((long)read(packet) << 24) + (read(packet) << 16) + (read(packet) << 8) + (read(packet) << 0);
    }
    
    public static long readLongLittle(final ByteSequence packet) throws IOException {
        spaceNeeded(packet, 8);
        return (read(packet) << 0) + (read(packet) << 8) + (read(packet) << 16) + ((long)read(packet) << 24) + ((long)read(packet) << 32) + ((long)read(packet) << 40) + ((long)read(packet) << 48) + ((long)read(packet) << 56);
    }
    
    public static double readDoubleBig(final ByteSequence packet) throws IOException {
        return Double.longBitsToDouble(readLongBig(packet));
    }
    
    public static double readDoubleLittle(final ByteSequence packet) throws IOException {
        return Double.longBitsToDouble(readLongLittle(packet));
    }
    
    public static float readFloatBig(final ByteSequence packet) throws IOException {
        return Float.intBitsToFloat(readIntBig(packet));
    }
    
    public static float readFloatLittle(final ByteSequence packet) throws IOException {
        return Float.intBitsToFloat(readIntLittle(packet));
    }
    
    public static void write(final ByteSequence packet, final int b) throws IOException {
        spaceNeeded(packet, 1);
        packet.data[packet.offset++] = (byte)b;
    }
    
    public static void write(final ByteSequence packet, final byte[] b) throws IOException {
        write(packet, b, 0, b.length);
    }
    
    public static void write(final ByteSequence packet, final byte[] b, final int off, final int len) throws IOException {
        spaceNeeded(packet, len);
        System.arraycopy(b, off, packet.data, packet.offset, len);
        packet.offset += len;
    }
    
    public static void writeBoolean(final ByteSequence packet, final boolean v) throws IOException {
        spaceNeeded(packet, 1);
        write(packet, v ? 1 : 0);
    }
    
    public static void writeByte(final ByteSequence packet, final int v) throws IOException {
        spaceNeeded(packet, 1);
        write(packet, v);
    }
    
    public static void writeShortBig(final ByteSequence packet, final int v) throws IOException {
        spaceNeeded(packet, 2);
        write(packet, v >>> 8 & 0xFF);
        write(packet, v >>> 0 & 0xFF);
    }
    
    public static void writeShortLittle(final ByteSequence packet, final int v) throws IOException {
        spaceNeeded(packet, 2);
        write(packet, v >>> 0 & 0xFF);
        write(packet, v >>> 8 & 0xFF);
    }
    
    public static void writeCharBig(final ByteSequence packet, final int v) throws IOException {
        spaceNeeded(packet, 2);
        write(packet, v >>> 8 & 0xFF);
        write(packet, v >>> 0 & 0xFF);
    }
    
    public static void writeCharLittle(final ByteSequence packet, final int v) throws IOException {
        spaceNeeded(packet, 2);
        write(packet, v >>> 0 & 0xFF);
        write(packet, v >>> 8 & 0xFF);
    }
    
    public static void writeIntBig(final ByteSequence packet, final int v) throws IOException {
        spaceNeeded(packet, 4);
        write(packet, v >>> 24 & 0xFF);
        write(packet, v >>> 16 & 0xFF);
        write(packet, v >>> 8 & 0xFF);
        write(packet, v >>> 0 & 0xFF);
    }
    
    public static void writeIntLittle(final ByteSequence packet, final int v) throws IOException {
        spaceNeeded(packet, 4);
        write(packet, v >>> 0 & 0xFF);
        write(packet, v >>> 8 & 0xFF);
        write(packet, v >>> 16 & 0xFF);
        write(packet, v >>> 24 & 0xFF);
    }
    
    public static void writeLongBig(final ByteSequence packet, final long v) throws IOException {
        spaceNeeded(packet, 8);
        write(packet, (int)(v >>> 56) & 0xFF);
        write(packet, (int)(v >>> 48) & 0xFF);
        write(packet, (int)(v >>> 40) & 0xFF);
        write(packet, (int)(v >>> 32) & 0xFF);
        write(packet, (int)(v >>> 24) & 0xFF);
        write(packet, (int)(v >>> 16) & 0xFF);
        write(packet, (int)(v >>> 8) & 0xFF);
        write(packet, (int)(v >>> 0) & 0xFF);
    }
    
    public static void writeLongLittle(final ByteSequence packet, final long v) throws IOException {
        spaceNeeded(packet, 8);
        write(packet, (int)(v >>> 0) & 0xFF);
        write(packet, (int)(v >>> 8) & 0xFF);
        write(packet, (int)(v >>> 16) & 0xFF);
        write(packet, (int)(v >>> 24) & 0xFF);
        write(packet, (int)(v >>> 32) & 0xFF);
        write(packet, (int)(v >>> 40) & 0xFF);
        write(packet, (int)(v >>> 48) & 0xFF);
        write(packet, (int)(v >>> 56) & 0xFF);
    }
    
    public static void writeDoubleBig(final ByteSequence packet, final double v) throws IOException {
        writeLongBig(packet, Double.doubleToLongBits(v));
    }
    
    public static void writeDoubleLittle(final ByteSequence packet, final double v) throws IOException {
        writeLongLittle(packet, Double.doubleToLongBits(v));
    }
    
    public static void writeFloatBig(final ByteSequence packet, final float v) throws IOException {
        writeIntBig(packet, Float.floatToIntBits(v));
    }
    
    public static void writeFloatLittle(final ByteSequence packet, final float v) throws IOException {
        writeIntLittle(packet, Float.floatToIntBits(v));
    }
    
    public static void writeRawDoubleBig(final ByteSequence packet, final double v) throws IOException {
        writeLongBig(packet, Double.doubleToRawLongBits(v));
    }
    
    public static void writeRawDoubleLittle(final ByteSequence packet, final double v) throws IOException {
        writeLongLittle(packet, Double.doubleToRawLongBits(v));
    }
    
    public static void writeRawFloatBig(final ByteSequence packet, final float v) throws IOException {
        writeIntBig(packet, Float.floatToRawIntBits(v));
    }
    
    public static void writeRawFloatLittle(final ByteSequence packet, final float v) throws IOException {
        writeIntLittle(packet, Float.floatToRawIntBits(v));
    }
}
