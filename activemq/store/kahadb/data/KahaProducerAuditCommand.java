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

public final class KahaProducerAuditCommand extends KahaProducerAuditCommandBase<KahaProducerAuditCommand> implements JournalCommand<KahaProducerAuditCommand>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasAudit()) {
            missingFields.add("audit");
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearAudit();
    }
    
    @Override
    public KahaProducerAuditCommand clone() {
        return new KahaProducerAuditCommand().mergeFrom(this);
    }
    
    @Override
    public KahaProducerAuditCommand mergeFrom(final KahaProducerAuditCommand other) {
        if (other.hasAudit()) {
            this.setAudit(other.getAudit());
        }
        return this;
    }
    
    @Override
    public int serializedSizeUnframed() {
        if (this.memoizedSerializedSize != -1) {
            return this.memoizedSerializedSize;
        }
        int size = 0;
        if (this.hasAudit()) {
            size += CodedOutputStream.computeBytesSize(1, this.getAudit());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaProducerAuditCommand mergeUnframed(final CodedInputStream input) throws IOException {
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
                    this.setAudit(input.readBytes());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasAudit()) {
            output.writeBytes(1, this.getAudit());
        }
    }
    
    public static KahaProducerAuditCommand parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaProducerAuditCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaProducerAuditCommand parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaProducerAuditCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaProducerAuditCommand parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaProducerAuditCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaProducerAuditCommand parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaProducerAuditCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaProducerAuditCommand parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaProducerAuditCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaProducerAuditCommand parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaProducerAuditCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaProducerAuditCommand parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaProducerAuditCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaProducerAuditCommand parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaProducerAuditCommand().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasAudit()) {
            sb.append(prefix + "audit: ");
            sb.append(this.getAudit());
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
        return KahaEntryType.KAHA_PRODUCER_AUDIT_COMMAND;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaProducerAuditCommand.class && this.equals((KahaProducerAuditCommand)obj));
    }
    
    public boolean equals(final KahaProducerAuditCommand obj) {
        return !(this.hasAudit() ^ obj.hasAudit()) && (!this.hasAudit() || this.getAudit().equals(obj.getAudit()));
    }
    
    @Override
    public int hashCode() {
        int rc = 691941169;
        if (this.hasAudit()) {
            rc ^= (0x3CAABBB ^ this.getAudit().hashCode());
        }
        return rc;
    }
}
