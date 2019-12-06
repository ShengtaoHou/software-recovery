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

public final class KahaAckMessageFileMapCommand extends KahaAckMessageFileMapCommandBase<KahaAckMessageFileMapCommand> implements JournalCommand<KahaAckMessageFileMapCommand>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasAckMessageFileMap()) {
            missingFields.add("ackMessageFileMap");
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearAckMessageFileMap();
    }
    
    @Override
    public KahaAckMessageFileMapCommand clone() {
        return new KahaAckMessageFileMapCommand().mergeFrom(this);
    }
    
    @Override
    public KahaAckMessageFileMapCommand mergeFrom(final KahaAckMessageFileMapCommand other) {
        if (other.hasAckMessageFileMap()) {
            this.setAckMessageFileMap(other.getAckMessageFileMap());
        }
        return this;
    }
    
    @Override
    public int serializedSizeUnframed() {
        if (this.memoizedSerializedSize != -1) {
            return this.memoizedSerializedSize;
        }
        int size = 0;
        if (this.hasAckMessageFileMap()) {
            size += CodedOutputStream.computeBytesSize(1, this.getAckMessageFileMap());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaAckMessageFileMapCommand mergeUnframed(final CodedInputStream input) throws IOException {
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
                    this.setAckMessageFileMap(input.readBytes());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasAckMessageFileMap()) {
            output.writeBytes(1, this.getAckMessageFileMap());
        }
    }
    
    public static KahaAckMessageFileMapCommand parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaAckMessageFileMapCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaAckMessageFileMapCommand parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaAckMessageFileMapCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaAckMessageFileMapCommand parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaAckMessageFileMapCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaAckMessageFileMapCommand parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaAckMessageFileMapCommand().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaAckMessageFileMapCommand parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaAckMessageFileMapCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaAckMessageFileMapCommand parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaAckMessageFileMapCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaAckMessageFileMapCommand parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaAckMessageFileMapCommand().mergeFramed(data).checktInitialized();
    }
    
    public static KahaAckMessageFileMapCommand parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaAckMessageFileMapCommand().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasAckMessageFileMap()) {
            sb.append(prefix + "ackMessageFileMap: ");
            sb.append(this.getAckMessageFileMap());
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
        return KahaEntryType.KAHA_ACK_MESSAGE_FILE_MAP_COMMAND;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaAckMessageFileMapCommand.class && this.equals((KahaAckMessageFileMapCommand)obj));
    }
    
    public boolean equals(final KahaAckMessageFileMapCommand obj) {
        return !(this.hasAckMessageFileMap() ^ obj.hasAckMessageFileMap()) && (!this.hasAckMessageFileMap() || this.getAckMessageFileMap().equals(obj.getAckMessageFileMap()));
    }
    
    @Override
    public int hashCode() {
        int rc = -259621928;
        if (this.hasAckMessageFileMap()) {
            rc ^= (0x9A5A902 ^ this.getAckMessageFileMap().hashCode());
        }
        return rc;
    }
}
