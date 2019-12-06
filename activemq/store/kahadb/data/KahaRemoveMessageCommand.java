// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.store.kahadb.Visitor;
import java.io.InputStream;
import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import org.apache.activemq.protobuf.CodedInputStream;
import org.apache.activemq.protobuf.CodedOutputStream;
import org.apache.activemq.protobuf.BaseMessage;
import org.apache.activemq.protobuf.UninitializedMessageException;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.activemq.store.kahadb.JournalCommand;

public final class KahaRemoveMessageCommand extends KahaRemoveMessageCommandBase<KahaRemoveMessageCommand> implements JournalCommand<KahaRemoveMessageCommand>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasDestination()) {
            missingFields.add("destination");
        }
        if (!this.hasMessageId()) {
            missingFields.add("messageId");
        }
        if (this.hasTransactionInfo()) {
            try {
                this.getTransactionInfo().assertInitialized();
            }
            catch (UninitializedMessageException e) {
                missingFields.addAll(this.prefix(e.getMissingFields(), "transaction_info."));
            }
        }
        if (this.hasDestination()) {
            try {
                this.getDestination().assertInitialized();
            }
            catch (UninitializedMessageException e) {
                missingFields.addAll(this.prefix(e.getMissingFields(), "destination."));
            }
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearTransactionInfo();
        this.clearDestination();
        this.clearMessageId();
        this.clearAck();
        this.clearSubscriptionKey();
    }
    
    @Override
    public KahaRemoveMessageCommand clone() {
        return new KahaRemoveMessageCommand().mergeFrom(this);
    }
    
    @Override
    public KahaRemoveMessageCommand mergeFrom(final KahaRemoveMessageCommand other) {
        if (other.hasTransactionInfo()) {
            if (this.hasTransactionInfo()) {
                this.getTransactionInfo().mergeFrom(other.getTransactionInfo());
            }
            else {
                this.setTransactionInfo(other.getTransactionInfo().clone());
            }
        }
        if (other.hasDestination()) {
            if (this.hasDestination()) {
                this.getDestination().mergeFrom(other.getDestination());
            }
            else {
                this.setDestination(other.getDestination().clone());
            }
        }
        if (other.hasMessageId()) {
            this.setMessageId(other.getMessageId());
        }
        if (other.hasAck()) {
            this.setAck(other.getAck());
        }
        if (other.hasSubscriptionKey()) {
            this.setSubscriptionKey(other.getSubscriptionKey());
        }
        return this;
    }
    
    @Override
    public int serializedSizeUnframed() {
        if (this.memoizedSerializedSize != -1) {
            return this.memoizedSerializedSize;
        }
        int size = 0;
        if (this.hasTransactionInfo()) {
            size += BaseMessage.computeMessageSize(1, (BaseMessage<Object>)this.getTransactionInfo());
        }
        if (this.hasDestination()) {
            size += BaseMessage.computeMessageSize(2, (BaseMessage<Object>)this.getDestination());
        }
        if (this.hasMessageId()) {
            size += CodedOutputStream.computeStringSize(3, this.getMessageId());
        }
        if (this.hasAck()) {
            size += CodedOutputStream.computeBytesSize(4, this.getAck());
        }
        if (this.hasSubscriptionKey()) {
            size += CodedOutputStream.computeStringSize(5, this.getSubscriptionKey());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaRemoveMessageCommand mergeUnframed(final CodedInputStream input) throws IOException {
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
                    if (this.hasTransactionInfo()) {
                        this.getTransactionInfo().mergeFramed(input);
                        continue;
                    }
                    this.setTransactionInfo(new KahaTransactionInfo().mergeFramed(input));
                    continue;
                }
                case 18: {
                    if (this.hasDestination()) {
                        this.getDestination().mergeFramed(input);
                        continue;
                    }
                    this.setDestination(new KahaDestination().mergeFramed(input));
                    continue;
                }
                case 26: {
                    this.setMessageId(input.readString());
                    continue;
                }
                case 34: {
                    this.setAck(input.readBytes());
                    continue;
                }
                case 42: {
                    this.setSubscriptionKey(input.readString());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasTransactionInfo()) {
            BaseMessage.writeMessage(output, 1, (BaseMessage<Object>)this.getTransactionInfo());
        }
        if (this.hasDestination()) {
            BaseMessage.writeMessage(output, 2, (BaseMessage<Object>)this.getDestination());
        }
        if (this.hasMessageId()) {
            output.writeString(3, this.getMessageId());
        }
        if (this.hasAck()) {
            output.writeBytes(4, this.getAck());
        }
        if (this.hasSubscriptionKey()) {
            output.writeString(5, this.getSubscriptionKey());
        }
    }
    
    public static KahaRemoveMessageCommand parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaRemoveMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaRemoveMessageCommand parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaRemoveMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaRemoveMessageCommand parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaRemoveMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaRemoveMessageCommand parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaRemoveMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaRemoveMessageCommand parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaRemoveMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaRemoveMessageCommand parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaRemoveMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaRemoveMessageCommand parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaRemoveMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaRemoveMessageCommand parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaRemoveMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasTransactionInfo()) {
            sb.append(prefix + "transaction_info {\n");
            this.getTransactionInfo().toString(sb, prefix + "  ");
            sb.append(prefix + "}\n");
        }
        if (this.hasDestination()) {
            sb.append(prefix + "destination {\n");
            this.getDestination().toString(sb, prefix + "  ");
            sb.append(prefix + "}\n");
        }
        if (this.hasMessageId()) {
            sb.append(prefix + "messageId: ");
            sb.append(this.getMessageId());
            sb.append("\n");
        }
        if (this.hasAck()) {
            sb.append(prefix + "ack: ");
            sb.append(this.getAck());
            sb.append("\n");
        }
        if (this.hasSubscriptionKey()) {
            sb.append(prefix + "subscriptionKey: ");
            sb.append(this.getSubscriptionKey());
            sb.append("\n");
        }
        return sb;
    }
    
    @Override
    public void visit(final Visitor visitor) throws IOException {
        visitor.visit(this);
    }
    
    @Override
    public KahaEntryType type() {
        return KahaEntryType.KAHA_REMOVE_MESSAGE_COMMAND;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaRemoveMessageCommand.class && this.equals((KahaRemoveMessageCommand)obj));
    }
    
    public boolean equals(final KahaRemoveMessageCommand obj) {
        return !(this.hasTransactionInfo() ^ obj.hasTransactionInfo()) && (!this.hasTransactionInfo() || this.getTransactionInfo().equals(obj.getTransactionInfo())) && !(this.hasDestination() ^ obj.hasDestination()) && (!this.hasDestination() || this.getDestination().equals(obj.getDestination())) && !(this.hasMessageId() ^ obj.hasMessageId()) && (!this.hasMessageId() || this.getMessageId().equals(obj.getMessageId())) && !(this.hasAck() ^ obj.hasAck()) && (!this.hasAck() || this.getAck().equals(obj.getAck())) && !(this.hasSubscriptionKey() ^ obj.hasSubscriptionKey()) && (!this.hasSubscriptionKey() || this.getSubscriptionKey().equals(obj.getSubscriptionKey()));
    }
    
    @Override
    public int hashCode() {
        int rc = -64211337;
        if (this.hasTransactionInfo()) {
            rc ^= (0xFD5C48C ^ this.getTransactionInfo().hashCode());
        }
        if (this.hasDestination()) {
            rc ^= (0xE2FEBEE ^ this.getDestination().hashCode());
        }
        if (this.hasMessageId()) {
            rc ^= (0x219D4362 ^ this.getMessageId().hashCode());
        }
        if (this.hasAck()) {
            rc ^= (0x10069 ^ this.getAck().hashCode());
        }
        if (this.hasSubscriptionKey()) {
            rc ^= (0x710013E2 ^ this.getSubscriptionKey().hashCode());
        }
        return rc;
    }
}
