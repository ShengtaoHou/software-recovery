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

public final class KahaXATransactionId extends KahaXATransactionIdBase<KahaXATransactionId>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasFormatId()) {
            missingFields.add("format_id");
        }
        if (!this.hasBranchQualifier()) {
            missingFields.add("branch_qualifier");
        }
        if (!this.hasGlobalTransactionId()) {
            missingFields.add("global_transaction_id");
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearFormatId();
        this.clearBranchQualifier();
        this.clearGlobalTransactionId();
    }
    
    @Override
    public KahaXATransactionId clone() {
        return new KahaXATransactionId().mergeFrom(this);
    }
    
    @Override
    public KahaXATransactionId mergeFrom(final KahaXATransactionId other) {
        if (other.hasFormatId()) {
            this.setFormatId(other.getFormatId());
        }
        if (other.hasBranchQualifier()) {
            this.setBranchQualifier(other.getBranchQualifier());
        }
        if (other.hasGlobalTransactionId()) {
            this.setGlobalTransactionId(other.getGlobalTransactionId());
        }
        return this;
    }
    
    @Override
    public int serializedSizeUnframed() {
        if (this.memoizedSerializedSize != -1) {
            return this.memoizedSerializedSize;
        }
        int size = 0;
        if (this.hasFormatId()) {
            size += CodedOutputStream.computeInt32Size(1, this.getFormatId());
        }
        if (this.hasBranchQualifier()) {
            size += CodedOutputStream.computeBytesSize(2, this.getBranchQualifier());
        }
        if (this.hasGlobalTransactionId()) {
            size += CodedOutputStream.computeBytesSize(3, this.getGlobalTransactionId());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaXATransactionId mergeUnframed(final CodedInputStream input) throws IOException {
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
                    this.setFormatId(input.readInt32());
                    continue;
                }
                case 18: {
                    this.setBranchQualifier(input.readBytes());
                    continue;
                }
                case 26: {
                    this.setGlobalTransactionId(input.readBytes());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasFormatId()) {
            output.writeInt32(1, this.getFormatId());
        }
        if (this.hasBranchQualifier()) {
            output.writeBytes(2, this.getBranchQualifier());
        }
        if (this.hasGlobalTransactionId()) {
            output.writeBytes(3, this.getGlobalTransactionId());
        }
    }
    
    public static KahaXATransactionId parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaXATransactionId().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaXATransactionId parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaXATransactionId().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaXATransactionId parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaXATransactionId().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaXATransactionId parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaXATransactionId().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaXATransactionId parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaXATransactionId().mergeFramed(data).checktInitialized();
    }
    
    public static KahaXATransactionId parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaXATransactionId().mergeFramed(data).checktInitialized();
    }
    
    public static KahaXATransactionId parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaXATransactionId().mergeFramed(data).checktInitialized();
    }
    
    public static KahaXATransactionId parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaXATransactionId().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasFormatId()) {
            sb.append(prefix + "format_id: ");
            sb.append(this.getFormatId());
            sb.append("\n");
        }
        if (this.hasBranchQualifier()) {
            sb.append(prefix + "branch_qualifier: ");
            sb.append(this.getBranchQualifier());
            sb.append("\n");
        }
        if (this.hasGlobalTransactionId()) {
            sb.append(prefix + "global_transaction_id: ");
            sb.append(this.getGlobalTransactionId());
            sb.append("\n");
        }
        return sb;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaXATransactionId.class && this.equals((KahaXATransactionId)obj));
    }
    
    public boolean equals(final KahaXATransactionId obj) {
        return !(this.hasFormatId() ^ obj.hasFormatId()) && (!this.hasFormatId() || this.getFormatId() == obj.getFormatId()) && !(this.hasBranchQualifier() ^ obj.hasBranchQualifier()) && (!this.hasBranchQualifier() || this.getBranchQualifier().equals(obj.getBranchQualifier())) && !(this.hasGlobalTransactionId() ^ obj.hasGlobalTransactionId()) && (!this.hasGlobalTransactionId() || this.getGlobalTransactionId().equals(obj.getGlobalTransactionId()));
    }
    
    @Override
    public int hashCode() {
        int rc = -2138302623;
        if (this.hasFormatId()) {
            rc ^= (0x201C4392 ^ this.getFormatId());
        }
        if (this.hasBranchQualifier()) {
            rc ^= (0x6CA6D908 ^ this.getBranchQualifier().hashCode());
        }
        if (this.hasGlobalTransactionId()) {
            rc ^= (0xEB55D196 ^ this.getGlobalTransactionId().hashCode());
        }
        return rc;
    }
}
