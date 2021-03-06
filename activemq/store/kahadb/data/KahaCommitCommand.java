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

public final class KahaCommitCommand extends KahaCommitCommandBase<KahaCommitCommand> implements JournalCommand<KahaCommitCommand>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasTransactionInfo()) {
            missingFields.add("transaction_info");
        }
        if (this.hasTransactionInfo()) {
            try {
                this.getTransactionInfo().assertInitialized();
            }
            catch (UninitializedMessageException e) {
                missingFields.addAll(this.prefix(e.getMissingFields(), "transaction_info."));
            }
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearTransactionInfo();
    }
    
    @Override
    public KahaCommitCommand clone() {
        return new KahaCommitCommand().mergeFrom(this);
    }
    
    @Override
    public KahaCommitCommand mergeFrom(final KahaCommitCommand other) {
        if (other.hasTransactionInfo()) {
            if (this.hasTransactionInfo()) {
                this.getTransactionInfo().mergeFrom(other.getTransactionInfo());
            }
            else {
                this.setTransactionInfo(other.getTransactionInfo().clone());
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
        if (this.hasTransactionInfo()) {
            size += BaseMessage.computeMessageSize(1, (BaseMessage<Object>)this.getTransactionInfo());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaCommitCommand mergeUnframed(final CodedInputStream input) throws IOException {
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
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasTransactionInfo()) {
            BaseMessage.writeMessage(output, 1, (BaseMessage<Object>)this.getTransactionInfo());
        }
    }
    
    public static KahaCommitCommand parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaCommitCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaCommitCommand parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaCommitCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaCommitCommand parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaCommitCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaCommitCommand parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaCommitCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaCommitCommand parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaCommitCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaCommitCommand parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaCommitCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaCommitCommand parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaCommitCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaCommitCommand parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaCommitCommand().mergeFramed(data).checktInitialized();
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
        return sb;
    }
    
    @Override
    public void visit(final Visitor visitor) throws IOException {
        visitor.visit(this);
    }
    
    @Override
    public KahaEntryType type() {
        return KahaEntryType.KAHA_COMMIT_COMMAND;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaCommitCommand.class && this.equals((KahaCommitCommand)obj));
    }
    
    public boolean equals(final KahaCommitCommand obj) {
        return !(this.hasTransactionInfo() ^ obj.hasTransactionInfo()) && (!this.hasTransactionInfo() || this.getTransactionInfo().equals(obj.getTransactionInfo()));
    }
    
    @Override
    public int hashCode() {
        int rc = -651907739;
        if (this.hasTransactionInfo()) {
            rc ^= (0xFD5C48C ^ this.getTransactionInfo().hashCode());
        }
        return rc;
    }
}
