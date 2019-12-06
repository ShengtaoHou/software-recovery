// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import java.io.InputStream;
import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import org.apache.activemq.protobuf.CodedInputStream;
import org.apache.activemq.protobuf.CodedOutputStream;
import java.util.ArrayList;

public final class KahaDestination extends KahaDestinationBase<KahaDestination>
{
    @Override
    public ArrayList<String> missingFields() {
        final ArrayList<String> missingFields = super.missingFields();
        if (!this.hasType()) {
            missingFields.add("type");
        }
        if (!this.hasName()) {
            missingFields.add("name");
        }
        return missingFields;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.clearType();
        this.clearName();
    }
    
    @Override
    public KahaDestination clone() {
        return new KahaDestination().mergeFrom(this);
    }
    
    @Override
    public KahaDestination mergeFrom(final KahaDestination other) {
        if (other.hasType()) {
            this.setType(other.getType());
        }
        if (other.hasName()) {
            this.setName(other.getName());
        }
        return this;
    }
    
    @Override
    public int serializedSizeUnframed() {
        if (this.memoizedSerializedSize != -1) {
            return this.memoizedSerializedSize;
        }
        int size = 0;
        if (this.hasType()) {
            size += CodedOutputStream.computeEnumSize(1, this.getType().getNumber());
        }
        if (this.hasName()) {
            size += CodedOutputStream.computeStringSize(2, this.getName());
        }
        return this.memoizedSerializedSize = size;
    }
    
    @Override
    public KahaDestination mergeUnframed(final CodedInputStream input) throws IOException {
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
                case 8: {
                    final int t = input.readEnum();
                    final DestinationType value = DestinationType.valueOf(t);
                    if (value == null) {
                        continue;
                    }
                    this.setType(value);
                    continue;
                }
                case 18: {
                    this.setName(input.readString());
                    continue;
                }
            }
        }
    }
    
    @Override
    public void writeUnframed(final CodedOutputStream output) throws IOException {
        if (this.hasType()) {
            output.writeEnum(1, this.getType().getNumber());
        }
        if (this.hasName()) {
            output.writeString(2, this.getName());
        }
    }
    
    public static KahaDestination parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaDestination().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaDestination parseUnframed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaDestination().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaDestination parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaDestination().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaDestination parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaDestination().mergeUnframed(data).checktInitialized();
    }
    
    public static KahaDestination parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaDestination().mergeFramed(data).checktInitialized();
    }
    
    public static KahaDestination parseFramed(final Buffer data) throws InvalidProtocolBufferException {
        return new KahaDestination().mergeFramed(data).checktInitialized();
    }
    
    public static KahaDestination parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return new KahaDestination().mergeFramed(data).checktInitialized();
    }
    
    public static KahaDestination parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return new KahaDestination().mergeFramed(data).checktInitialized();
    }
    
    @Override
    public String toString() {
        return this.toString(new StringBuilder(), "").toString();
    }
    
    public StringBuilder toString(final StringBuilder sb, final String prefix) {
        if (this.hasType()) {
            sb.append(prefix + "type: ");
            sb.append(this.getType());
            sb.append("\n");
        }
        if (this.hasName()) {
            sb.append(prefix + "name: ");
            sb.append(this.getName());
            sb.append("\n");
        }
        return sb;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && obj.getClass() == KahaDestination.class && this.equals((KahaDestination)obj));
    }
    
    public boolean equals(final KahaDestination obj) {
        return !(this.hasType() ^ obj.hasType()) && (!this.hasType() || this.getType().equals(obj.getType())) && !(this.hasName() ^ obj.hasName()) && (!this.hasName() || this.getName().equals(obj.getName()));
    }
    
    @Override
    public int hashCode() {
        int rc = -972308577;
        if (this.hasType()) {
            rc ^= (0x28035A ^ this.getType().hashCode());
        }
        if (this.hasName()) {
            rc ^= (0x24EEAB ^ this.getName().hashCode());
        }
        return rc;
    }
    
    public enum DestinationType
    {
        QUEUE("QUEUE", 0), 
        TOPIC("TOPIC", 1), 
        TEMP_QUEUE("TEMP_QUEUE", 2), 
        TEMP_TOPIC("TEMP_TOPIC", 3);
        
        private final String name;
        private final int value;
        
        private DestinationType(final String name, final int value) {
            this.name = name;
            this.value = value;
        }
        
        public final int getNumber() {
            return this.value;
        }
        
        @Override
        public final String toString() {
            return this.name;
        }
        
        public static DestinationType valueOf(final int value) {
            switch (value) {
                case 0: {
                    return DestinationType.QUEUE;
                }
                case 1: {
                    return DestinationType.TOPIC;
                }
                case 2: {
                    return DestinationType.TEMP_QUEUE;
                }
                case 3: {
                    return DestinationType.TEMP_TOPIC;
                }
                default: {
                    return null;
                }
            }
        }
    }
}
