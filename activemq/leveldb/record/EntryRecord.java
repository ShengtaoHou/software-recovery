// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.record;

import java.io.OutputStream;
import org.fusesource.hawtbuf.proto.CodedOutputStream;
import java.io.DataOutput;
import java.io.DataInput;
import org.fusesource.hawtbuf.proto.PBMessage;
import org.fusesource.hawtbuf.proto.MessageBuffer;
import org.fusesource.hawtbuf.proto.MessageBufferSupport;
import org.fusesource.hawtbuf.Buffer;
import java.io.InputStream;
import java.io.IOException;
import org.fusesource.hawtbuf.proto.InvalidProtocolBufferException;
import org.fusesource.hawtbuf.proto.CodedInputStream;
import org.fusesource.hawtbuf.proto.PBMessageUnframedCodec;
import org.fusesource.hawtbuf.proto.PBMessageFramedCodec;
import org.fusesource.hawtbuf.proto.PBMessageFactory;

public class EntryRecord implements PBMessageFactory<Bean, Buffer>
{
    public static final EntryRecord FACTORY;
    public static final PBMessageFramedCodec<Buffer> FRAMED_CODEC;
    public static final PBMessageUnframedCodec<Buffer> UNFRAMED_CODEC;
    
    public Bean create() {
        return new Bean();
    }
    
    public Bean parseUnframed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        return new Bean().mergeUnframed(data);
    }
    
    public Bean parseUnframed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return this.parseUnframed(new CodedInputStream(data));
    }
    
    public Buffer parseUnframed(final org.fusesource.hawtbuf.Buffer data) throws InvalidProtocolBufferException {
        return new Buffer(data);
    }
    
    public Buffer parseUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return this.parseUnframed(new org.fusesource.hawtbuf.Buffer(data));
    }
    
    public Buffer parseFramed(final CodedInputStream data) throws InvalidProtocolBufferException, IOException {
        final int length = data.readRawVarint32();
        final int oldLimit = data.pushLimit(length);
        final Buffer rc = this.parseUnframed(data.readRawBytes(length));
        data.popLimit(oldLimit);
        return rc;
    }
    
    public Buffer parseFramed(final org.fusesource.hawtbuf.Buffer data) throws InvalidProtocolBufferException {
        try {
            final CodedInputStream input = new CodedInputStream(data);
            final Buffer rc = this.parseFramed(input);
            input.checkLastTagWas(0);
            return rc;
        }
        catch (InvalidProtocolBufferException e) {
            throw e;
        }
        catch (IOException e2) {
            throw new RuntimeException("An IOException was thrown (should never happen in this method).", e2);
        }
    }
    
    public Buffer parseFramed(final byte[] data) throws InvalidProtocolBufferException {
        return this.parseFramed(new org.fusesource.hawtbuf.Buffer(data));
    }
    
    public Buffer parseFramed(final InputStream data) throws InvalidProtocolBufferException, IOException {
        return this.parseUnframed(MessageBufferSupport.readFrame(data));
    }
    
    static {
        FACTORY = new EntryRecord();
        FRAMED_CODEC = new PBMessageFramedCodec((PBMessageFactory)EntryRecord.FACTORY);
        UNFRAMED_CODEC = new PBMessageUnframedCodec((PBMessageFactory)EntryRecord.FACTORY);
    }
    
    public static final class Bean implements Getter
    {
        Buffer frozen;
        Bean bean;
        private long f_collectionKey;
        private boolean b_collectionKey;
        private org.fusesource.hawtbuf.Buffer f_entryKey;
        private long f_valueLocation;
        private boolean b_valueLocation;
        private int f_valueLength;
        private boolean b_valueLength;
        private org.fusesource.hawtbuf.Buffer f_value;
        private org.fusesource.hawtbuf.Buffer f_meta;
        
        public Bean() {
            this.f_collectionKey = 0L;
            this.f_entryKey = null;
            this.f_valueLocation = 0L;
            this.f_valueLength = 0;
            this.f_value = null;
            this.f_meta = null;
            this.bean = this;
        }
        
        public Bean(final Bean copy) {
            this.f_collectionKey = 0L;
            this.f_entryKey = null;
            this.f_valueLocation = 0L;
            this.f_valueLength = 0;
            this.f_value = null;
            this.f_meta = null;
            this.bean = copy;
        }
        
        @Override
        public Bean copy() {
            return new Bean(this.bean);
        }
        
        public boolean frozen() {
            return this.frozen != null;
        }
        
        @Override
        public Buffer freeze() {
            if (this.frozen == null) {
                this.frozen = new Buffer(this.bean);
                assert this.deepFreeze();
            }
            return this.frozen;
        }
        
        private boolean deepFreeze() {
            this.frozen.serializedSizeUnframed();
            return true;
        }
        
        private void copyCheck() {
            assert this.frozen == null : "Modification not allowed after object has been fozen.  Try modifying a copy of this object.";
            if (this.bean != this) {
                this.copy(this.bean);
            }
        }
        
        private void copy(final Bean other) {
            this.bean = this;
            this.f_collectionKey = other.f_collectionKey;
            this.b_collectionKey = other.b_collectionKey;
            this.f_entryKey = other.f_entryKey;
            this.f_valueLocation = other.f_valueLocation;
            this.b_valueLocation = other.b_valueLocation;
            this.f_valueLength = other.f_valueLength;
            this.b_valueLength = other.b_valueLength;
            this.f_value = other.f_value;
            this.f_meta = other.f_meta;
        }
        
        @Override
        public boolean hasCollectionKey() {
            return this.bean.b_collectionKey;
        }
        
        @Override
        public long getCollectionKey() {
            return this.bean.f_collectionKey;
        }
        
        public Bean setCollectionKey(final long collectionKey) {
            this.copyCheck();
            this.b_collectionKey = true;
            this.f_collectionKey = collectionKey;
            return this;
        }
        
        public void clearCollectionKey() {
            this.copyCheck();
            this.b_collectionKey = false;
            this.f_collectionKey = 0L;
        }
        
        @Override
        public boolean hasEntryKey() {
            return this.bean.f_entryKey != null;
        }
        
        @Override
        public org.fusesource.hawtbuf.Buffer getEntryKey() {
            return this.bean.f_entryKey;
        }
        
        public Bean setEntryKey(final org.fusesource.hawtbuf.Buffer entryKey) {
            this.copyCheck();
            this.f_entryKey = entryKey;
            return this;
        }
        
        public void clearEntryKey() {
            this.copyCheck();
            this.f_entryKey = null;
        }
        
        @Override
        public boolean hasValueLocation() {
            return this.bean.b_valueLocation;
        }
        
        @Override
        public long getValueLocation() {
            return this.bean.f_valueLocation;
        }
        
        public Bean setValueLocation(final long valueLocation) {
            this.copyCheck();
            this.b_valueLocation = true;
            this.f_valueLocation = valueLocation;
            return this;
        }
        
        public void clearValueLocation() {
            this.copyCheck();
            this.b_valueLocation = false;
            this.f_valueLocation = 0L;
        }
        
        @Override
        public boolean hasValueLength() {
            return this.bean.b_valueLength;
        }
        
        @Override
        public int getValueLength() {
            return this.bean.f_valueLength;
        }
        
        public Bean setValueLength(final int valueLength) {
            this.copyCheck();
            this.b_valueLength = true;
            this.f_valueLength = valueLength;
            return this;
        }
        
        public void clearValueLength() {
            this.copyCheck();
            this.b_valueLength = false;
            this.f_valueLength = 0;
        }
        
        @Override
        public boolean hasValue() {
            return this.bean.f_value != null;
        }
        
        @Override
        public org.fusesource.hawtbuf.Buffer getValue() {
            return this.bean.f_value;
        }
        
        public Bean setValue(final org.fusesource.hawtbuf.Buffer value) {
            this.copyCheck();
            this.f_value = value;
            return this;
        }
        
        public void clearValue() {
            this.copyCheck();
            this.f_value = null;
        }
        
        @Override
        public boolean hasMeta() {
            return this.bean.f_meta != null;
        }
        
        @Override
        public org.fusesource.hawtbuf.Buffer getMeta() {
            return this.bean.f_meta;
        }
        
        public Bean setMeta(final org.fusesource.hawtbuf.Buffer meta) {
            this.copyCheck();
            this.f_meta = meta;
            return this;
        }
        
        public void clearMeta() {
            this.copyCheck();
            this.f_meta = null;
        }
        
        @Override
        public String toString() {
            return this.toString(new StringBuilder(), "").toString();
        }
        
        @Override
        public StringBuilder toString(final StringBuilder sb, final String prefix) {
            if (this.hasCollectionKey()) {
                sb.append(prefix + "collection_key: ");
                sb.append(this.getCollectionKey());
                sb.append("\n");
            }
            if (this.hasEntryKey()) {
                sb.append(prefix + "entry_key: ");
                sb.append(this.getEntryKey());
                sb.append("\n");
            }
            if (this.hasValueLocation()) {
                sb.append(prefix + "value_location: ");
                sb.append(this.getValueLocation());
                sb.append("\n");
            }
            if (this.hasValueLength()) {
                sb.append(prefix + "value_length: ");
                sb.append(this.getValueLength());
                sb.append("\n");
            }
            if (this.hasValue()) {
                sb.append(prefix + "value: ");
                sb.append(this.getValue());
                sb.append("\n");
            }
            if (this.hasMeta()) {
                sb.append(prefix + "meta: ");
                sb.append(this.getMeta());
                sb.append("\n");
            }
            return sb;
        }
        
        public Bean mergeUnframed(final InputStream input) throws IOException {
            return this.mergeUnframed(new CodedInputStream(input));
        }
        
        public Bean mergeUnframed(final CodedInputStream input) throws IOException {
            this.copyCheck();
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
                        this.setCollectionKey(input.readInt64());
                        continue;
                    }
                    case 18: {
                        this.setEntryKey(input.readBytes());
                        continue;
                    }
                    case 24: {
                        this.setValueLocation(input.readInt64());
                        continue;
                    }
                    case 32: {
                        this.setValueLength(input.readInt32());
                        continue;
                    }
                    case 42: {
                        this.setValue(input.readBytes());
                        continue;
                    }
                    case 50: {
                        this.setMeta(input.readBytes());
                        continue;
                    }
                }
            }
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj != null && obj.getClass() == Bean.class && this.equals((Bean)obj));
        }
        
        public boolean equals(final Bean obj) {
            return !(this.hasCollectionKey() ^ obj.hasCollectionKey()) && (!this.hasCollectionKey() || this.getCollectionKey() == obj.getCollectionKey()) && !(this.hasEntryKey() ^ obj.hasEntryKey()) && (!this.hasEntryKey() || this.getEntryKey().equals(obj.getEntryKey())) && !(this.hasValueLocation() ^ obj.hasValueLocation()) && (!this.hasValueLocation() || this.getValueLocation() == obj.getValueLocation()) && !(this.hasValueLength() ^ obj.hasValueLength()) && (!this.hasValueLength() || this.getValueLength() == obj.getValueLength()) && !(this.hasValue() ^ obj.hasValue()) && (!this.hasValue() || this.getValue().equals(obj.getValue())) && !(this.hasMeta() ^ obj.hasMeta()) && (!this.hasMeta() || this.getMeta().equals(obj.getMeta()));
        }
        
        @Override
        public int hashCode() {
            int rc = 2066384;
            if (this.hasCollectionKey()) {
                rc ^= (0xFEABBAC1 ^ new Long(this.getCollectionKey()).hashCode());
            }
            if (this.hasEntryKey()) {
                rc ^= (0x870F54AD ^ this.getEntryKey().hashCode());
            }
            if (this.hasValueLocation()) {
                rc ^= (0x7489C426 ^ new Long(this.getValueLocation()).hashCode());
            }
            if (this.hasValueLength()) {
                rc ^= (0x1B8C797 ^ this.getValueLength());
            }
            if (this.hasValue()) {
                rc ^= (0x4E9A151 ^ this.getValue().hashCode());
            }
            if (this.hasMeta()) {
                rc ^= (0x248A25 ^ this.getMeta().hashCode());
            }
            return rc;
        }
        
        public Bean mergeFrom(final Getter other) {
            this.copyCheck();
            if (other.hasCollectionKey()) {
                this.setCollectionKey(other.getCollectionKey());
            }
            if (other.hasEntryKey()) {
                this.setEntryKey(other.getEntryKey());
            }
            if (other.hasValueLocation()) {
                this.setValueLocation(other.getValueLocation());
            }
            if (other.hasValueLength()) {
                this.setValueLength(other.getValueLength());
            }
            if (other.hasValue()) {
                this.setValue(other.getValue());
            }
            if (other.hasMeta()) {
                this.setMeta(other.getMeta());
            }
            return this;
        }
        
        public void clear() {
            this.clearCollectionKey();
            this.clearEntryKey();
            this.clearValueLocation();
            this.clearValueLength();
            this.clearValue();
            this.clearMeta();
        }
        
        public void readExternal(final DataInput in) throws IOException {
            assert this.frozen == null : "Modification not allowed after object has been fozen.  Try modifying a copy of this object.";
            this.bean = this;
            this.frozen = null;
            this.f_collectionKey = in.readLong();
            this.b_collectionKey = true;
            int size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_entryKey = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_entryKey = null;
            }
            this.f_valueLocation = in.readLong();
            this.b_valueLocation = true;
            this.f_valueLength = in.readInt();
            this.b_valueLength = true;
            size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_value = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_value = null;
            }
            size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_meta = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_meta = null;
            }
        }
        
        public void writeExternal(final DataOutput out) throws IOException {
            out.writeLong(this.bean.f_collectionKey);
            if (this.bean.f_entryKey != null) {
                out.writeInt(this.bean.f_entryKey.getLength());
                out.write(this.bean.f_entryKey.getData(), this.bean.f_entryKey.getOffset(), this.bean.f_entryKey.getLength());
            }
            else {
                out.writeInt(-1);
            }
            out.writeLong(this.bean.f_valueLocation);
            out.writeInt(this.bean.f_valueLength);
            if (this.bean.f_value != null) {
                out.writeInt(this.bean.f_value.getLength());
                out.write(this.bean.f_value.getData(), this.bean.f_value.getOffset(), this.bean.f_value.getLength());
            }
            else {
                out.writeInt(-1);
            }
            if (this.bean.f_meta != null) {
                out.writeInt(this.bean.f_meta.getLength());
                out.write(this.bean.f_meta.getData(), this.bean.f_meta.getOffset(), this.bean.f_meta.getLength());
            }
            else {
                out.writeInt(-1);
            }
        }
    }
    
    public static final class Buffer implements MessageBuffer<Bean, Buffer>, Getter
    {
        private Bean bean;
        private org.fusesource.hawtbuf.Buffer buffer;
        private int size;
        private int hashCode;
        
        private Buffer(final org.fusesource.hawtbuf.Buffer buffer) {
            this.size = -1;
            this.buffer = buffer;
        }
        
        private Buffer(final Bean bean) {
            this.size = -1;
            this.bean = bean;
        }
        
        public Bean copy() {
            return this.bean().copy();
        }
        
        public Buffer freeze() {
            return this;
        }
        
        private Bean bean() {
            if (this.bean == null) {
                try {
                    this.bean = new Bean().mergeUnframed(new CodedInputStream(this.buffer));
                    this.bean.frozen = this;
                }
                catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException((Throwable)e);
                }
                catch (IOException e2) {
                    throw new RuntimeException("An IOException was thrown (should never happen in this method).", e2);
                }
            }
            return this.bean;
        }
        
        @Override
        public String toString() {
            return this.bean().toString();
        }
        
        public StringBuilder toString(final StringBuilder sb, final String prefix) {
            return this.bean().toString(sb, prefix);
        }
        
        public boolean hasCollectionKey() {
            return this.bean().hasCollectionKey();
        }
        
        public long getCollectionKey() {
            return this.bean().getCollectionKey();
        }
        
        public boolean hasEntryKey() {
            return this.bean().hasEntryKey();
        }
        
        public org.fusesource.hawtbuf.Buffer getEntryKey() {
            return this.bean().getEntryKey();
        }
        
        public boolean hasValueLocation() {
            return this.bean().hasValueLocation();
        }
        
        public long getValueLocation() {
            return this.bean().getValueLocation();
        }
        
        public boolean hasValueLength() {
            return this.bean().hasValueLength();
        }
        
        public int getValueLength() {
            return this.bean().getValueLength();
        }
        
        public boolean hasValue() {
            return this.bean().hasValue();
        }
        
        public org.fusesource.hawtbuf.Buffer getValue() {
            return this.bean().getValue();
        }
        
        public boolean hasMeta() {
            return this.bean().hasMeta();
        }
        
        public org.fusesource.hawtbuf.Buffer getMeta() {
            return this.bean().getMeta();
        }
        
        public org.fusesource.hawtbuf.Buffer toUnframedBuffer() {
            if (this.buffer != null) {
                return this.buffer;
            }
            return MessageBufferSupport.toUnframedBuffer((MessageBuffer)this);
        }
        
        public org.fusesource.hawtbuf.Buffer toFramedBuffer() {
            return MessageBufferSupport.toFramedBuffer((MessageBuffer)this);
        }
        
        public byte[] toUnframedByteArray() {
            return this.toUnframedBuffer().toByteArray();
        }
        
        public byte[] toFramedByteArray() {
            return this.toFramedBuffer().toByteArray();
        }
        
        public void writeFramed(final CodedOutputStream output) throws IOException {
            output.writeRawVarint32(this.serializedSizeUnframed());
            this.writeUnframed(output);
        }
        
        public void writeFramed(final OutputStream output) throws IOException {
            final CodedOutputStream codedOutput = new CodedOutputStream(output);
            this.writeFramed(codedOutput);
            codedOutput.flush();
        }
        
        public void writeUnframed(final OutputStream output) throws IOException {
            final CodedOutputStream codedOutput = new CodedOutputStream(output);
            this.writeUnframed(codedOutput);
            codedOutput.flush();
        }
        
        public void writeUnframed(CodedOutputStream output) throws IOException {
            if (this.buffer == null) {
                final int size = this.serializedSizeUnframed();
                this.buffer = output.getNextBuffer(size);
                CodedOutputStream original = null;
                if (this.buffer == null) {
                    this.buffer = new org.fusesource.hawtbuf.Buffer(new byte[size]);
                    original = output;
                    output = new CodedOutputStream(this.buffer);
                }
                if (this.bean.hasCollectionKey()) {
                    output.writeInt64(1, this.bean.getCollectionKey());
                }
                if (this.bean.hasEntryKey()) {
                    output.writeBytes(2, this.bean.getEntryKey());
                }
                if (this.bean.hasValueLocation()) {
                    output.writeInt64(3, this.bean.getValueLocation());
                }
                if (this.bean.hasValueLength()) {
                    output.writeInt32(4, this.bean.getValueLength());
                }
                if (this.bean.hasValue()) {
                    output.writeBytes(5, this.bean.getValue());
                }
                if (this.bean.hasMeta()) {
                    output.writeBytes(6, this.bean.getMeta());
                }
                if (original != null) {
                    output.checkNoSpaceLeft();
                    output = original;
                    output.writeRawBytes(this.buffer);
                }
            }
            else {
                output.writeRawBytes(this.buffer);
            }
        }
        
        public int serializedSizeFramed() {
            final int t = this.serializedSizeUnframed();
            return CodedOutputStream.computeRawVarint32Size(t) + t;
        }
        
        public int serializedSizeUnframed() {
            if (this.buffer != null) {
                return this.buffer.length;
            }
            if (this.size != -1) {
                return this.size;
            }
            this.size = 0;
            if (this.hasCollectionKey()) {
                this.size += CodedOutputStream.computeInt64Size(1, this.getCollectionKey());
            }
            if (this.hasEntryKey()) {
                this.size += CodedOutputStream.computeBytesSize(2, this.getEntryKey());
            }
            if (this.hasValueLocation()) {
                this.size += CodedOutputStream.computeInt64Size(3, this.getValueLocation());
            }
            if (this.hasValueLength()) {
                this.size += CodedOutputStream.computeInt32Size(4, this.getValueLength());
            }
            if (this.hasValue()) {
                this.size += CodedOutputStream.computeBytesSize(5, this.getValue());
            }
            if (this.hasMeta()) {
                this.size += CodedOutputStream.computeBytesSize(6, this.getMeta());
            }
            return this.size;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj != null && obj.getClass() == Buffer.class && this.equals((Buffer)obj));
        }
        
        public boolean equals(final Buffer obj) {
            return this.toUnframedBuffer().equals(obj.toUnframedBuffer());
        }
        
        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                this.hashCode = (0x77408060 ^ this.toUnframedBuffer().hashCode());
            }
            return this.hashCode;
        }
        
        public boolean frozen() {
            return true;
        }
    }
    
    public interface Getter extends PBMessage<Bean, Buffer>
    {
        boolean hasCollectionKey();
        
        long getCollectionKey();
        
        boolean hasEntryKey();
        
        org.fusesource.hawtbuf.Buffer getEntryKey();
        
        boolean hasValueLocation();
        
        long getValueLocation();
        
        boolean hasValueLength();
        
        int getValueLength();
        
        boolean hasValue();
        
        org.fusesource.hawtbuf.Buffer getValue();
        
        boolean hasMeta();
        
        org.fusesource.hawtbuf.Buffer getMeta();
        
        Bean copy();
        
        Buffer freeze();
        
        StringBuilder toString(final StringBuilder p0, final String p1);
    }
}
