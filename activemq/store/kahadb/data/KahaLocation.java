// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import java.io.InputStream;
import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import org.apache.activemq.protobuf.CodedInputStream;
import org.apache.activemq.protobuf.CodedOutputStream;
import java.util.ArrayList;

public final class KahaLocation extends KahaLocationBase<KahaLocation>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasLogId()) {
            missingFields.add("log_id");
        }
        if (!this.hasOffset()) {
            missingFields.add("offset");
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearLogId();
        this.clearOffset();
    }
    
    @Override
    public KahaLocation clone() {
        return new KahaLocation().mergeFrom(this);
    }
    
    @Override
    public KahaLocation mergeFrom(final KahaLocation other) {
        if (other.hasLogId()) {
            this.setLogId(other.getLogId());
        }
        if (other.hasOffset()) {
            this.setOffset(other.getOffset());
        }
        return this;
    }
    
    @Override
    public int serializedSizeUnframed() {
        if (this.memoizedSerializedSize != -1) {
            return this.memoizedSerializedSize;
        }
        int size = 0;
        if (this.hasLogId()) {
            size += CodedOutputStream.computeInt32Size(1, this.getLogId());
        }
        if (this.hasOffset()) {
            size += CodedOutputStream.computeInt32Size(2, this.getOffset());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaLocation mergeUnframed(final CodedInputStream input) throws IOException {
        while (true) {
            final int tag = input.readTag();
            if ((tag & 0x7) == 0x4) {
                return this;
            }
            switch (tag) {
                case 0: {
                    return this;
                }
                default: {
                    continue;
                }
                case 8: {
                    this.setLogId(input.readInt32());
                    continue;
                }
                case 16: {
                    this.setOffset(input.readInt32());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasLogId()) {
            output.writeInt32(1, this.getLogId());
        }
        if (this.hasOffset()) {
            output.writeInt32(2, this.getOffset());
        }
    }
    
    public static KahaLocation parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaLocation().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaLocation parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaLocation().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaLocation parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaLocation().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaLocation parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaLocation().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaLocation parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaLocation().mergeFramed(data).checktInitialized();
    }
    
    public static KahaLocation parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaLocation().mergeFramed(data).checktInitialized();
    }
    
    public static KahaLocation parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaLocation().mergeFramed(data).checktInitialized();
    }
    
    public static KahaLocation parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaLocation().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasLogId()) {
            sb.append(prefix + "log_id: ");
            sb.append(this.getLogId());
            sb.append("\n");
        }
        if (this.hasOffset()) {
            sb.append(prefix + "offset: ");
            sb.append(this.getOffset());
            sb.append("\n");
        }
        return sb;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaLocation.class && this.equals((KahaLocation)obj));
    }
    
    public boolean equals(final KahaLocation obj) {
        return !(this.hasLogId() ^ obj.hasLogId()) && (!this.hasLogId() || this.getLogId() == obj.getLogId()) && !(this.hasOffset() ^ obj.hasOffset()) && (!this.hasOffset() || this.getOffset() == obj.getOffset());
    }
    
    @Override
    public int hashCode() {
        int rc = -1935591996;
        if (this.hasLogId()) {
            rc ^= (0x462FB5F ^ this.getLogId());
        }
        if (this.hasOffset()) {
            rc ^= (0x8C9C50B3 ^ this.getOffset());
        }
        return rc;
    }
}
