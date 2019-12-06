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

public class EntryKey implements PBMessageFactory<Bean, Buffer>
{
    public static final EntryKey FACTORY;
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
        FACTORY = new EntryKey();
        FRAMED_CODEC = new PBMessageFramedCodec((PBMessageFactory)EntryKey.FACTORY);
        UNFRAMED_CODEC = new PBMessageUnframedCodec((PBMessageFactory)EntryKey.FACTORY);
    }
    
    public static final class Bean implements Getter
    {
        Buffer frozen;
        Bean bean;
        private long f_collectionKey;
        private boolean b_collectionKey;
        private org.fusesource.hawtbuf.Buffer f_entryKey;
        
        public Bean() {
            this.f_collectionKey = 0L;
            this.f_entryKey = null;
            this.bean = this;
        }
        
        public Bean(final Bean copy) {
            this.f_collectionKey = 0L;
            this.f_entryKey = null;
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
                }
            }
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj != null && obj.getClass() == Bean.class && this.equals((Bean)obj));
        }
        
        public boolean equals(final Bean obj) {
            return !(this.hasCollectionKey() ^ obj.hasCollectionKey()) && (!this.hasCollectionKey() || this.getCollectionKey() == obj.getCollectionKey()) && !(this.hasEntryKey() ^ obj.hasEntryKey()) && (!this.hasEntryKey() || this.getEntryKey().equals(obj.getEntryKey()));
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
            return this;
        }
        
        public void clear() {
            this.clearCollectionKey();
            this.clearEntryKey();
        }
        
        public void readExternal(final DataInput in) throws IOException {
            assert this.frozen == null : "Modification not allowed after object has been fozen.  Try modifying a copy of this object.";
            this.bean = this;
            this.frozen = null;
            this.f_collectionKey = in.readLong();
            this.b_collectionKey = true;
            final int size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_entryKey = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_entryKey = null;
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
                output.writeInt64(1, this.bean.getCollectionKey());
                output.writeBytes(2, this.bean.getEntryKey());
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
            this.size += CodedOutputStream.computeInt64Size(1, this.getCollectionKey());
            return this.size += CodedOutputStream.computeBytesSize(2, this.getEntryKey());
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
        
        Bean copy();
        
        Buffer freeze();
        
        StringBuilder toString(final StringBuilder p0, final String p1);
    }
}
