// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.io.DataInput;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.DataOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BitArray implements Serializable
{
    private static final long serialVersionUID = 1L;
    static final int LONG_SIZE = 64;
    static final int INT_SIZE = 32;
    static final int SHORT_SIZE = 16;
    static final int BYTE_SIZE = 8;
    private static final long[] BIT_VALUES;
    private long bits;
    private int length;
    
    public int length() {
        return this.length;
    }
    
    public long getBits() {
        return this.bits;
    }
    
    public boolean set(final int index, final boolean flag) {
        this.length = Math.max(this.length, index + 1);
        final boolean oldValue = (this.bits & BitArray.BIT_VALUES[index]) != 0x0L;
        if (flag) {
            this.bits |= BitArray.BIT_VALUES[index];
        }
        else if (oldValue) {
            this.bits &= ~BitArray.BIT_VALUES[index];
        }
        return oldValue;
    }
    
    public boolean get(final int index) {
        return (this.bits & BitArray.BIT_VALUES[index]) != 0x0L;
    }
    
    public void reset() {
        this.bits = 0L;
    }
    
    public void reset(final long bits) {
        this.bits = bits;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        this.writeToStream(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.readFromStream(in);
    }
    
    public void writeToStream(final DataOutput dataOut) throws IOException {
        dataOut.writeByte(this.length);
        if (this.length <= 8) {
            dataOut.writeByte((byte)this.bits);
        }
        else if (this.length <= 16) {
            dataOut.writeShort((short)this.bits);
        }
        else if (this.length <= 32) {
            dataOut.writeInt((int)this.bits);
        }
        else {
            dataOut.writeLong(this.bits);
        }
    }
    
    public void readFromStream(final DataInput dataIn) throws IOException {
        this.length = dataIn.readByte();
        if (this.length <= 8) {
            this.bits = dataIn.readByte();
        }
        else if (this.length <= 16) {
            this.bits = dataIn.readShort();
        }
        else if (this.length <= 32) {
            this.bits = dataIn.readInt();
        }
        else {
            this.bits = dataIn.readLong();
        }
    }
    
    static {
        BIT_VALUES = new long[] { 1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L, 1024L, 2048L, 4096L, 8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 1048576L, 2097152L, 4194304L, 8388608L, 16777216L, 33554432L, 67108864L, 134217728L, 268435456L, 536870912L, 1073741824L, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, Long.MIN_VALUE };
    }
}
