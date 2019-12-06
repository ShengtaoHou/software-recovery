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

public final class KahaLocalTransactionId extends KahaLocalTransactionIdBase<KahaLocalTransactionId>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasConnectionId()) {
            missingFields.add("connection_id");
        }
        if (!this.hasTransactionId()) {
            missingFields.add("transaction_id");
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearConnectionId();
        this.clearTransactionId();
    }
    
    @Override
    public KahaLocalTransactionId clone() {
        return new KahaLocalTransactionId().mergeFrom(this);
    }
    
    @Override
    public KahaLocalTransactionId mergeFrom(final KahaLocalTransactionId other) {
        if (other.hasConnectionId()) {
            this.setConnectionId(other.getConnectionId());
        }
        if (other.hasTransactionId()) {
            this.setTransactionId(other.getTransactionId());
        }
        return this;
    }
    
    @Override
    public int serializedSizeUnframed() {
        if (this.memoizedSerializedSize != -1) {
            return this.memoizedSerializedSize;
        }
        int size = 0;
        if (this.hasConnectionId()) {
            size += CodedOutputStream.computeStringSize(1, this.getConnectionId());
        }
        if (this.hasTransactionId()) {
            size += CodedOutputStream.computeInt64Size(1, this.getTransactionId());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaLocalTransactionId mergeUnframed(final CodedInputStream input) throws IOException {
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
                case 10: {
                    this.setConnectionId(input.readString());
                    continue;
                }
                case 8: {
                    this.setTransactionId(input.readInt64());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasConnectionId()) {
            output.writeString(1, this.getConnectionId());
        }
        if (this.hasTransactionId()) {
            output.writeInt64(1, this.getTransactionId());
        }
    }
    
    public static KahaLocalTransactionId parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaLocalTransactionId().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaLocalTransactionId parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaLocalTransactionId().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaLocalTransactionId parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaLocalTransactionId().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaLocalTransactionId parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaLocalTransactionId().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaLocalTransactionId parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaLocalTransactionId().mergeFramed(data).checktInitialized();
    }
    
    public static KahaLocalTransactionId parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaLocalTransactionId().mergeFramed(data).checktInitialized();
    }
    
    public static KahaLocalTransactionId parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaLocalTransactionId().mergeFramed(data).checktInitialized();
    }
    
    public static KahaLocalTransactionId parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaLocalTransactionId().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasConnectionId()) {
            sb.append(prefix + "connection_id: ");
            sb.append(this.getConnectionId());
            sb.append("\n");
        }
        if (this.hasTransactionId()) {
            sb.append(prefix + "transaction_id: ");
            sb.append(this.getTransactionId());
            sb.append("\n");
        }
        return sb;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaLocalTransactionId.class && this.equals((KahaLocalTransactionId)obj));
    }
    
    public boolean equals(final KahaLocalTransactionId obj) {
        return !(this.hasConnectionId() ^ obj.hasConnectionId()) && (!this.hasConnectionId() || this.getConnectionId().equals(obj.getConnectionId())) && !(this.hasTransactionId() ^ obj.hasTransactionId()) && (!this.hasTransactionId() || this.getTransactionId() == obj.getTransactionId());
    }
    
    @Override
    public int hashCode() {
        int rc = 1725637181;
        if (this.hasConnectionId()) {
            rc ^= (0x7C6B9CB9 ^ this.getConnectionId().hashCode());
        }
        if (this.hasTransactionId()) {
            rc ^= (0x4A56CC79 ^ new Long(this.getTransactionId()).hashCode());
        }
        return rc;
    }
}
