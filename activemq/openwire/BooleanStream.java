// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire;

import java.io.DataInput;
import java.nio.ByteBuffer;
import java.io.DataOutput;
import java.io.IOException;

public final class BooleanStream
{
    byte[] data;
    short arrayLimit;
    short arrayPos;
    byte bytePos;
    
    public BooleanStream() {
        this.data = new byte[48];
    }
    
    public boolean readBoolean() throws IOException {
        assert this.arrayPos <= this.arrayLimit;
        final byte b = this.data[this.arrayPos];
        final boolean rc = (b >> this.bytePos & 0x1) != 0x0;
        ++this.bytePos;
        if (this.bytePos >= 8) {
            this.bytePos = 0;
            ++this.arrayPos;
        }
        return rc;
    }
    
    public void writeBoolean(final boolean value) throws IOException {
        if (this.bytePos == 0) {
            ++this.arrayLimit;
            if (this.arrayLimit >= this.data.length) {
                final byte[] d = new byte[this.data.length * 2];
                System.arraycopy(this.data, 0, d, 0, this.data.length);
                this.data = d;
            }
        }
        if (value) {
            final byte[] data = this.data;
            final short arrayPos = this.arrayPos;
            data[arrayPos] |= (byte)(1 << this.bytePos);
        }
        ++this.bytePos;
        if (this.bytePos >= 8) {
            this.bytePos = 0;
            ++this.arrayPos;
        }
    }
    
    public void marshal(final DataOutput dataOut) throws IOException {
        if (this.arrayLimit < 64) {
            dataOut.writeByte(this.arrayLimit);
        }
        else if (this.arrayLimit < 256) {
            dataOut.writeByte(192);
            dataOut.writeByte(this.arrayLimit);
        }
        else {
            dataOut.writeByte(128);
            dataOut.writeShort(this.arrayLimit);
        }
        dataOut.write(this.data, 0, this.arrayLimit);
        this.clear();
    }
    
    public void marshal(final ByteBuffer dataOut) {
        if (this.arrayLimit < 64) {
            dataOut.put((byte)this.arrayLimit);
        }
        else if (this.arrayLimit < 256) {
            dataOut.put((byte)(-64));
            dataOut.put((byte)this.arrayLimit);
        }
        else {
            dataOut.put((byte)(-128));
            dataOut.putShort(this.arrayLimit);
        }
        dataOut.put(this.data, 0, this.arrayLimit);
    }
    
    public void unmarshal(final DataInput dataIn) throws IOException {
        this.arrayLimit = (short)(dataIn.readByte() & 0xFF);
        if (this.arrayLimit == 192) {
            this.arrayLimit = (short)(dataIn.readByte() & 0xFF);
        }
        else if (this.arrayLimit == 128) {
            this.arrayLimit = dataIn.readShort();
        }
        if (this.data.length < this.arrayLimit) {
            this.data = new byte[this.arrayLimit];
        }
        dataIn.readFully(this.data, 0, this.arrayLimit);
        this.clear();
    }
    
    public void clear() {
        this.arrayPos = 0;
        this.bytePos = 0;
    }
    
    public int marshalledSize() {
        if (this.arrayLimit < 64) {
            return 1 + this.arrayLimit;
        }
        if (this.arrayLimit < 256) {
            return 2 + this.arrayLimit;
        }
        return 3 + this.arrayLimit;
    }
}
