// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import java.io.InputStream;
import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.InvalidProtocolBufferException;
import org.apache.activemq.protobuf.CodedOutputStream;
import java.io.IOException;
import org.apache.activemq.protobuf.CodedInputStream;
import org.apache.activemq.protobuf.BaseMessage;
import org.apache.activemq.protobuf.UninitializedMessageException;
import java.util.Collection;
import java.util.ArrayList;

public final class KahaTransactionInfo extends KahaTransactionInfoBase<KahaTransactionInfo>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (this.hasLocalTransactionId()) {
            try {
                this.getLocalTransactionId().assertInitialized();
            }
            catch (UninitializedMessageException e) {
                missingFields.addAll(this.prefix(e.getMissingFields(), "local_transaction_id."));
            }
        }
        if (this.hasXaTransactionId()) {
            try {
                this.getXaTransactionId().assertInitialized();
            }
            catch (UninitializedMessageException e) {
                missingFields.addAll(this.prefix(e.getMissingFields(), "xa_transaction_id."));
            }
        }
        if (this.hasPreviousEntry()) {
            try {
                this.getPreviousEntry().assertInitialized();
            }
            catch (UninitializedMessageException e) {
                missingFields.addAll(this.prefix(e.getMissingFields(), "previous_entry."));
            }
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearLocalTransactionId();
        this.clearXaTransactionId();
        this.clearPreviousEntry();
    }
    
    @Override
    public KahaTransactionInfo clone() {
        return new KahaTransactionInfo().mergeFrom(this);
    }
    
    @Override
    public KahaTransactionInfo mergeFrom(final KahaTransactionInfo other) {
        if (other.hasLocalTransactionId()) {
            if (this.hasLocalTransactionId()) {
                this.getLocalTransactionId().mergeFrom(other.getLocalTransactionId());
            }
            else {
                this.setLocalTransactionId(other.getLocalTransactionId().clone());
            }
        }
        if (other.hasXaTransactionId()) {
            if (this.hasXaTransactionId()) {
                this.getXaTransactionId().mergeFrom(other.getXaTransactionId());
            }
            else {
                this.setXaTransactionId(other.getXaTransactionId().clone());
            }
        }
        if (other.hasPreviousEntry()) {
            if (this.hasPreviousEntry()) {
                this.getPreviousEntry().mergeFrom(other.getPreviousEntry());
            }
            else {
                this.setPreviousEntry(other.getPreviousEntry().clone());
            }
        }
        return this;
    }
    
    @Override
    public int serializedSizeUnframed() {
        if (this.memoizedSerializedSize != -1) {
            return this.memoizedSerializedSize;
        }
        int size = 0;
        if (this.hasLocalTransactionId()) {
            size += BaseMessage.computeMessageSize(1, (BaseMessage<Object>)this.getLocalTransactionId());
        }
        if (this.hasXaTransactionId()) {
            size += BaseMessage.computeMessageSize(2, (BaseMessage<Object>)this.getXaTransactionId());
        }
        if (this.hasPreviousEntry()) {
            size += BaseMessage.computeMessageSize(3, (BaseMessage<Object>)this.getPreviousEntry());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaTransactionInfo mergeUnframed(final CodedInputStream input) throws IOException {
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
                    if (this.hasLocalTransactionId()) {
                        this.getLocalTransactionId().mergeFramed(input);
                        continue;
                    }
                    this.setLocalTransactionId(new KahaLocalTransactionId().mergeFramed(input));
                    continue;
                }
                case 18: {
                    if (this.hasXaTransactionId()) {
                        this.getXaTransactionId().mergeFramed(input);
                        continue;
                    }
                    this.setXaTransactionId(new KahaXATransactionId().mergeFramed(input));
                    continue;
                }
                case 26: {
                    if (this.hasPreviousEntry()) {
                        this.getPreviousEntry().mergeFramed(input);
                        continue;
                    }
                    this.setPreviousEntry(new KahaLocation().mergeFramed(input));
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasLocalTransactionId()) {
            BaseMessage.writeMessage(output, 1, (BaseMessage<Object>)this.getLocalTransactionId());
        }
        if (this.hasXaTransactionId()) {
            BaseMessage.writeMessage(output, 2, (BaseMessage<Object>)this.getXaTransactionId());
        }
        if (this.hasPreviousEntry()) {
            BaseMessage.writeMessage(output, 3, (BaseMessage<Object>)this.getPreviousEntry());
        }
    }
    
    public static KahaTransactionInfo parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaTransactionInfo().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaTransactionInfo parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaTransactionInfo().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaTransactionInfo parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaTransactionInfo().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaTransactionInfo parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaTransactionInfo().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaTransactionInfo parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaTransactionInfo().mergeFramed(data).checktInitialized();
    }
    
    public static KahaTransactionInfo parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaTransactionInfo().mergeFramed(data).checktInitialized();
    }
    
    public static KahaTransactionInfo parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaTransactionInfo().mergeFramed(data).checktInitialized();
    }
    
    public static KahaTransactionInfo parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaTransactionInfo().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasLocalTransactionId()) {
            sb.append(prefix + "local_transaction_id {\n");
            this.getLocalTransactionId().toString(sb, prefix + "  ");
            sb.append(prefix + "}\n");
        }
        if (this.hasXaTransactionId()) {
            sb.append(prefix + "xa_transaction_id {\n");
            this.getXaTransactionId().toString(sb, prefix + "  ");
            sb.append(prefix + "}\n");
        }
        if (this.hasPreviousEntry()) {
            sb.append(prefix + "previous_entry {\n");
            this.getPreviousEntry().toString(sb, prefix + "  ");
            sb.append(prefix + "}\n");
        }
        return sb;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaTransactionInfo.class && this.equals((KahaTransactionInfo)obj));
    }
    
    public boolean equals(final KahaTransactionInfo obj) {
        return !(this.hasLocalTransactionId() ^ obj.hasLocalTransactionId()) && (!this.hasLocalTransactionId() || this.getLocalTransactionId().equals(obj.getLocalTransactionId())) && !(this.hasXaTransactionId() ^ obj.hasXaTransactionId()) && (!this.hasXaTransactionId() || this.getXaTransactionId().equals(obj.getXaTransactionId())) && !(this.hasPreviousEntry() ^ obj.hasPreviousEntry()) && (!this.hasPreviousEntry() || this.getPreviousEntry().equals(obj.getPreviousEntry()));
    }
    
    @Override
    public int hashCode() {
        int rc = 156129213;
        if (this.hasLocalTransactionId()) {
            rc ^= (0x306A4F0E ^ this.getLocalTransactionId().hashCode());
        }
        if (this.hasXaTransactionId()) {
            rc ^= (0xC2CCB810 ^ this.getXaTransactionId().hashCode());
        }
        if (this.hasPreviousEntry()) {
            rc ^= (0x1E4CCF9B ^ this.getPreviousEntry().hashCode());
        }
        return rc;
    }
}
