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

public final class KahaAddMessageCommand extends KahaAddMessageCommandBase<KahaAddMessageCommand> implements JournalCommand<KahaAddMessageCommand>
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
        if (!this.hasMessage()) {
            missingFields.add("message");
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
        this.clearMessage();
        this.clearPriority();
        this.clearPrioritySupported();
    }
    
    @Override
    public KahaAddMessageCommand clone() {
        return new KahaAddMessageCommand().mergeFrom(this);
    }
    
    @Override
    public KahaAddMessageCommand mergeFrom(final KahaAddMessageCommand other) {
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
        if (other.hasMessage()) {
            this.setMessage(other.getMessage());
        }
        if (other.hasPriority()) {
            this.setPriority(other.getPriority());
        }
        if (other.hasPrioritySupported()) {
            this.setPrioritySupported(other.getPrioritySupported());
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
        if (this.hasMessage()) {
            size += CodedOutputStream.computeBytesSize(4, this.getMessage());
        }
        if (this.hasPriority()) {
            size += CodedOutputStream.computeInt32Size(5, this.getPriority());
        }
        if (this.hasPrioritySupported()) {
            size += CodedOutputStream.computeBoolSize(6, this.getPrioritySupported());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaAddMessageCommand mergeUnframed(final CodedInputStream input) throws IOException {
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
                    this.setMessage(input.readBytes());
                    continue;
                }
                case 40: {
                    this.setPriority(input.readInt32());
                    continue;
                }
                case 48: {
                    this.setPrioritySupported(input.readBool());
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
        if (this.hasMessage()) {
            output.writeBytes(4, this.getMessage());
        }
        if (this.hasPriority()) {
            output.writeInt32(5, this.getPriority());
        }
        if (this.hasPrioritySupported()) {
            output.writeBool(6, this.getPrioritySupported());
        }
    }
    
    public static KahaAddMessageCommand parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaAddMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaAddMessageCommand parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaAddMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaAddMessageCommand parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaAddMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaAddMessageCommand parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaAddMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaAddMessageCommand parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaAddMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaAddMessageCommand parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaAddMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaAddMessageCommand parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaAddMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaAddMessageCommand parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaAddMessageCommand().mergeFramed(data).checktInitialized();
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
        if (this.hasMessage()) {
            sb.append(prefix + "message: ");
            sb.append(this.getMessage());
            sb.append("\n");
        }
        if (this.hasPriority()) {
            sb.append(prefix + "priority: ");
            sb.append(this.getPriority());
            sb.append("\n");
        }
        if (this.hasPrioritySupported()) {
            sb.append(prefix + "prioritySupported: ");
            sb.append(this.getPrioritySupported());
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
        return KahaEntryType.KAHA_ADD_MESSAGE_COMMAND;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaAddMessageCommand.class && this.equals((KahaAddMessageCommand)obj));
    }
    
    public boolean equals(final KahaAddMessageCommand obj) {
        return !(this.hasTransactionInfo() ^ obj.hasTransactionInfo()) && (!this.hasTransactionInfo() || this.getTransactionInfo().equals(obj.getTransactionInfo())) && !(this.hasDestination() ^ obj.hasDestination()) && (!this.hasDestination() || this.getDestination().equals(obj.getDestination())) && !(this.hasMessageId() ^ obj.hasMessageId()) && (!this.hasMessageId() || this.getMessageId().equals(obj.getMessageId())) && !(this.hasMessage() ^ obj.hasMessage()) && (!this.hasMessage() || this.getMessage().equals(obj.getMessage())) && !(this.hasPriority() ^ obj.hasPriority()) && (!this.hasPriority() || this.getPriority() == obj.getPriority()) && !(this.hasPrioritySupported() ^ obj.hasPrioritySupported()) && (!this.hasPrioritySupported() || this.getPrioritySupported() == obj.getPrioritySupported());
    }
    
    @Override
    public int hashCode() {
        int rc = 1601475350;
        if (this.hasTransactionInfo()) {
            rc ^= (0xFD5C48C ^ this.getTransactionInfo().hashCode());
        }
        if (this.hasDestination()) {
            rc ^= (0xE2FEBEE ^ this.getDestination().hashCode());
        }
        if (this.hasMessageId()) {
            rc ^= (0x219D4362 ^ this.getMessageId().hashCode());
        }
        if (this.hasMessage()) {
            rc ^= (0x9C2397E7 ^ this.getMessage().hashCode());
        }
        if (this.hasPriority()) {
            rc ^= (0xBE62DDC4 ^ this.getPriority());
        }
        if (this.hasPrioritySupported()) {
            rc ^= (0x3504534A ^ (this.getPrioritySupported() ? 6 : -6));
        }
        return rc;
    }
}
