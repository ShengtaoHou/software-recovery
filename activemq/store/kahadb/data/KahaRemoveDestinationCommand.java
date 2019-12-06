// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.store.kahadb.Visitor;
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
import org.apache.activemq.store.kahadb.JournalCommand;

public final class KahaRemoveDestinationCommand extends KahaRemoveDestinationCommandBase<KahaRemoveDestinationCommand> implements JournalCommand<KahaRemoveDestinationCommand>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasDestination()) {
            missingFields.add("destination");
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
    }
    
    @Override
    public KahaRemoveDestinationCommand clone() {
        return new KahaRemoveDestinationCommand().mergeFrom(this);
    }
    
    @Override
    public KahaRemoveDestinationCommand mergeFrom(final KahaRemoveDestinationCommand other) {
        if (other.hasDestination()) {
            if (this.hasDestination()) {
                this.getDestination().mergeFrom(other.getDestination());
            }
            else {
                this.setDestination(other.getDestination().clone());
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
        if (this.hasDestination()) {
            size += BaseMessage.computeMessageSize(1, (BaseMessage<Object>)this.getDestination());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaRemoveDestinationCommand mergeUnframed(final CodedInputStream input) throws IOException {
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
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasDestination()) {
            BaseMessage.writeMessage(output, 1, (BaseMessage<Object>)this.getDestination());
        }
    }
    
    public static KahaRemoveDestinationCommand parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaRemoveDestinationCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaRemoveDestinationCommand parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaRemoveDestinationCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaRemoveDestinationCommand parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaRemoveDestinationCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaRemoveDestinationCommand parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaRemoveDestinationCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaRemoveDestinationCommand parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaRemoveDestinationCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaRemoveDestinationCommand parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaRemoveDestinationCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaRemoveDestinationCommand parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaRemoveDestinationCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaRemoveDestinationCommand parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaRemoveDestinationCommand().mergeFramed(data).checktInitialized();
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
        return sb;
    }
    
    @Override
    public void visit(final Visitor visitor) throws IOException {
        visitor.visit(this);
    }
    
    @Override
    public KahaEntryType type() {
        return KahaEntryType.KAHA_REMOVE_DESTINATION_COMMAND;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaRemoveDestinationCommand.class && this.equals((KahaRemoveDestinationCommand)obj));
    }
    
    public boolean equals(final KahaRemoveDestinationCommand obj) {
        return !(this.hasDestination() ^ obj.hasDestination()) && (!this.hasDestination() || this.getDestination().equals(obj.getDestination()));
    }
    
    @Override
    public int hashCode() {
        int rc = 302570256;
        if (this.hasDestination()) {
            rc ^= (0xE2FEBEE ^ this.getDestination().hashCode());
        }
        return rc;
    }
}
