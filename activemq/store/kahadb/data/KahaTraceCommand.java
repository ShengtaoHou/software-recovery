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
import java.util.ArrayList;
import org.apache.activemq.store.kahadb.JournalCommand;

public final class KahaTraceCommand extends KahaTraceCommandBase<KahaTraceCommand> implements JournalCommand<KahaTraceCommand>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasMessage()) {
            missingFields.add("message");
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearMessage();
    }
    
    @Override
    public KahaTraceCommand clone() {
        return new KahaTraceCommand().mergeFrom(this);
    }
    
    @Override
    public KahaTraceCommand mergeFrom(final KahaTraceCommand other) {
        if (other.hasMessage()) {
            this.setMessage(other.getMessage());
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
            size += CodedOutputStream.computeStringSize(1, this.getMessage());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaTraceCommand mergeUnframed(final CodedInputStream input) throws IOException {
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
                    this.setMessage(input.readString());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasMessage()) {
            output.writeString(1, this.getMessage());
        }
    }
    
    public static KahaTraceCommand parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaTraceCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaTraceCommand parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaTraceCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaTraceCommand parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaTraceCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaTraceCommand parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaTraceCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaTraceCommand parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaTraceCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaTraceCommand parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaTraceCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaTraceCommand parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaTraceCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaTraceCommand parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaTraceCommand().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasMessage()) {
            sb.append(prefix + "message: ");
            sb.append(this.getMessage());
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
        return KahaEntryType.KAHA_TRACE_COMMAND;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaTraceCommand.class && this.equals((KahaTraceCommand)obj));
    }
    
    public boolean equals(final KahaTraceCommand obj) {
        return !(this.hasMessage() ^ obj.hasMessage()) && (!this.hasMessage() || this.getMessage().equals(obj.getMessage()));
    }
    
    @Override
    public int hashCode() {
        int rc = -1782549291;
        if (this.hasMessage()) {
            rc ^= (0x9C2397E7 ^ this.getMessage().hashCode());
        }
        return rc;
    }
}
