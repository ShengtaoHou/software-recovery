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

public final class KahaUpdateMessageCommand extends KahaUpdateMessageCommandBase<KahaUpdateMessageCommand> implements JournalCommand<KahaUpdateMessageCommand>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasMessage()) {
            missingFields.add("message");
        }
        if (this.hasMessage()) {
            try {
                this.getMessage().assertInitialized();
            }
            catch (UninitializedMessageException e) {
                missingFields.addAll(this.prefix(e.getMissingFields(), "message."));
            }
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearMessage();
    }
    
    @Override
    public KahaUpdateMessageCommand clone() {
        return new KahaUpdateMessageCommand().mergeFrom(this);
    }
    
    @Override
    public KahaUpdateMessageCommand mergeFrom(final KahaUpdateMessageCommand other) {
        if (other.hasMessage()) {
            if (this.hasMessage()) {
                this.getMessage().mergeFrom(other.getMessage());
            }
            else {
                this.setMessage(other.getMessage().clone());
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
        if (this.hasMessage()) {
            size += BaseMessage.computeMessageSize(1, (BaseMessage<Object>)this.getMessage());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaUpdateMessageCommand mergeUnframed(final CodedInputStream input) throws IOException {
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
                    if (this.hasMessage()) {
                        this.getMessage().mergeFramed(input);
                        continue;
                    }
                    this.setMessage(new KahaAddMessageCommand().mergeFramed(input));
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasMessage()) {
            BaseMessage.writeMessage(output, 1, (BaseMessage<Object>)this.getMessage());
        }
    }
    
    public static KahaUpdateMessageCommand parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaUpdateMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaUpdateMessageCommand parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaUpdateMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaUpdateMessageCommand parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaUpdateMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaUpdateMessageCommand parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaUpdateMessageCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaUpdateMessageCommand parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaUpdateMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaUpdateMessageCommand parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaUpdateMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaUpdateMessageCommand parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaUpdateMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaUpdateMessageCommand parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaUpdateMessageCommand().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasMessage()) {
            sb.append(prefix + "message {\n");
            this.getMessage().toString(sb, prefix + "  ");
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
        return KahaEntryType.KAHA_UPDATE_MESSAGE_COMMAND;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaUpdateMessageCommand.class && this.equals((KahaUpdateMessageCommand)obj));
    }
    
    public boolean equals(final KahaUpdateMessageCommand obj) {
        return !(this.hasMessage() ^ obj.hasMessage()) && (!this.hasMessage() || this.getMessage().equals(obj.getMessage()));
    }
    
    @Override
    public int hashCode() {
        int rc = 1943345660;
        if (this.hasMessage()) {
            rc ^= (0x9C2397E7 ^ this.getMessage().hashCode());
        }
        return rc;
    }
}
