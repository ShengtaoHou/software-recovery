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

public final class KahaSubscriptionCommand extends KahaSubscriptionCommandBase<KahaSubscriptionCommand> implements JournalCommand<KahaSubscriptionCommand>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasDestination()) {
            missingFields.add("destination");
        }
        if (!this.hasSubscriptionKey()) {
            missingFields.add("subscriptionKey");
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
        this.clearDestination();
        this.clearSubscriptionKey();
        this.clearRetroactive();
        this.clearSubscriptionInfo();
    }
    
    @Override
    public KahaSubscriptionCommand clone() {
        return new KahaSubscriptionCommand().mergeFrom(this);
    }
    
    @Override
    public KahaSubscriptionCommand mergeFrom(final KahaSubscriptionCommand other) {
        if (other.hasDestination()) {
            if (this.hasDestination()) {
                this.getDestination().mergeFrom(other.getDestination());
            }
            else {
                this.setDestination(other.getDestination().clone());
            }
        }
        if (other.hasSubscriptionKey()) {
            this.setSubscriptionKey(other.getSubscriptionKey());
        }
        if (other.hasRetroactive()) {
            this.setRetroactive(other.getRetroactive());
        }
        if (other.hasSubscriptionInfo()) {
            this.setSubscriptionInfo(other.getSubscriptionInfo());
        }
        return this;
    }
    
    @Override
    public int serializedSizeUnframed() {
        if (this.memoizedSerializedSize != -1) {
            return this.memoizedSerializedSize;
        }
        int size = 0;
        if (this.hasDestination()) {
            size += BaseMessage.computeMessageSize(1, (BaseMessage<Object>)this.getDestination());
        }
        if (this.hasSubscriptionKey()) {
            size += CodedOutputStream.computeStringSize(2, this.getSubscriptionKey());
        }
        if (this.hasRetroactive()) {
            size += CodedOutputStream.computeBoolSize(3, this.getRetroactive());
        }
        if (this.hasSubscriptionInfo()) {
            size += CodedOutputStream.computeBytesSize(4, this.getSubscriptionInfo());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaSubscriptionCommand mergeUnframed(final CodedInputStream input) throws IOException {
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
                    if (this.hasDestination()) {
                        this.getDestination().mergeFramed(input);
                        continue;
                    }
                    this.setDestination(new KahaDestination().mergeFramed(input));
                    continue;
                }
                case 18: {
                    this.setSubscriptionKey(input.readString());
                    continue;
                }
                case 24: {
                    this.setRetroactive(input.readBool());
                    continue;
                }
                case 34: {
                    this.setSubscriptionInfo(input.readBytes());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasDestination()) {
            BaseMessage.writeMessage(output, 1, (BaseMessage<Object>)this.getDestination());
        }
        if (this.hasSubscriptionKey()) {
            output.writeString(2, this.getSubscriptionKey());
        }
        if (this.hasRetroactive()) {
            output.writeBool(3, this.getRetroactive());
        }
        if (this.hasSubscriptionInfo()) {
            output.writeBytes(4, this.getSubscriptionInfo());
        }
    }
    
    public static KahaSubscriptionCommand parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaSubscriptionCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaSubscriptionCommand parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaSubscriptionCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaSubscriptionCommand parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaSubscriptionCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaSubscriptionCommand parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaSubscriptionCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaSubscriptionCommand parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaSubscriptionCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaSubscriptionCommand parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaSubscriptionCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaSubscriptionCommand parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaSubscriptionCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaSubscriptionCommand parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaSubscriptionCommand().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasDestination()) {
            sb.append(prefix + "destination {\n");
            this.getDestination().toString(sb, prefix + "  ");
            sb.append(prefix + "}\n");
        }
        if (this.hasSubscriptionKey()) {
            sb.append(prefix + "subscriptionKey: ");
            sb.append(this.getSubscriptionKey());
            sb.append("\n");
        }
        if (this.hasRetroactive()) {
            sb.append(prefix + "retroactive: ");
            sb.append(this.getRetroactive());
            sb.append("\n");
        }
        if (this.hasSubscriptionInfo()) {
            sb.append(prefix + "subscriptionInfo: ");
            sb.append(this.getSubscriptionInfo());
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
        return KahaEntryType.KAHA_SUBSCRIPTION_COMMAND;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaSubscriptionCommand.class && this.equals((KahaSubscriptionCommand)obj));
    }
    
    public boolean equals(final KahaSubscriptionCommand obj) {
        return !(this.hasDestination() ^ obj.hasDestination()) && (!this.hasDestination() || this.getDestination().equals(obj.getDestination())) && !(this.hasSubscriptionKey() ^ obj.hasSubscriptionKey()) && (!this.hasSubscriptionKey() || this.getSubscriptionKey().equals(obj.getSubscriptionKey())) && !(this.hasRetroactive() ^ obj.hasRetroactive()) && (!this.hasRetroactive() || this.getRetroactive() == obj.getRetroactive()) && !(this.hasSubscriptionInfo() ^ obj.hasSubscriptionInfo()) && (!this.hasSubscriptionInfo() || this.getSubscriptionInfo().equals(obj.getSubscriptionInfo()));
    }
    
    @Override
    public int hashCode() {
        int rc = 172060159;
        if (this.hasDestination()) {
            rc ^= (0xE2FEBEE ^ this.getDestination().hashCode());
        }
        if (this.hasSubscriptionKey()) {
            rc ^= (0x710013E2 ^ this.getSubscriptionKey().hashCode());
        }
        if (this.hasRetroactive()) {
            rc ^= (0x1E865B04 ^ (this.getRetroactive() ? 3 : -3));
        }
        if (this.hasSubscriptionInfo()) {
            rc ^= (0xAF019F8B ^ this.getSubscriptionInfo().hashCode());
        }
        return rc;
    }
}
