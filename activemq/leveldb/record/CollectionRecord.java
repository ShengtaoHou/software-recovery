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

public class CollectionRecord implements PBMessageFactory<Bean, Buffer>
{
    public static final CollectionRecord FACTORY;
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
        FACTORY = new CollectionRecord();
        FRAMED_CODEC = new PBMessageFramedCodec((PBMessageFactory)CollectionRecord.FACTORY);
        UNFRAMED_CODEC = new PBMessageUnframedCodec((PBMessageFactory)CollectionRecord.FACTORY);
    }
    
    public static final class Bean implements Getter
    {
        Buffer frozen;
        Bean bean;
        private long f_key;
        private boolean b_key;
        private int f_type;
        private boolean b_type;
        private org.fusesource.hawtbuf.Buffer f_meta;
        
        public Bean() {
            this.f_key = 0L;
            this.f_type = 0;
            this.f_meta = null;
            this.bean = this;
        }
        
        public Bean(final Bean copy) {
            this.f_key = 0L;
            this.f_type = 0;
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
            this.f_key = other.f_key;
            this.b_key = other.b_key;
            this.f_type = other.f_type;
            this.b_type = other.b_type;
            this.f_meta = other.f_meta;
        }
        
        @Override
        public boolean hasKey() {
            return this.bean.b_key;
        }
        
        @Override
        public long getKey() {
            return this.bean.f_key;
        }
        
        public Bean setKey(final long key) {
            this.copyCheck();
            this.b_key = true;
            this.f_key = key;
            return this;
        }
        
        public void clearKey() {
            this.copyCheck();
            this.b_key = false;
            this.f_key = 0L;
        }
        
        @Override
        public boolean hasType() {
            return this.bean.b_type;
        }
        
        @Override
        public int getType() {
            return this.bean.f_type;
        }
        
        public Bean setType(final int type) {
            this.copyCheck();
            this.b_type = true;
            this.f_type = type;
            return this;
        }
        
        public void clearType() {
            this.copyCheck();
            this.b_type = false;
            this.f_type = 0;
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
            if (this.hasKey()) {
                sb.append(prefix + "key: ");
                sb.append(this.getKey());
                sb.append("\n");
            }
            if (this.hasType()) {
                sb.append(prefix + "type: ");
                sb.append(this.getType());
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
                        this.setKey(input.readInt64());
                        continue;
                    }
                    case 16: {
                        this.setType(input.readInt32());
                        continue;
                    }
                    case 26: {
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
            return !(this.hasKey() ^ obj.hasKey()) && (!this.hasKey() || this.getKey() == obj.getKey()) && !(this.hasType() ^ obj.hasType()) && (!this.hasType() || this.getType() == obj.getType()) && !(this.hasMeta() ^ obj.hasMeta()) && (!this.hasMeta() || this.getMeta().equals(obj.getMeta()));
        }
        
        @Override
        public int hashCode() {
            int rc = 2066384;
            if (this.hasKey()) {
                rc ^= (0x1263F ^ new Long(this.getKey()).hashCode());
            }
            if (this.hasType()) {
                rc ^= (0x28035A ^ this.getType());
            }
            if (this.hasMeta()) {
                rc ^= (0x248A25 ^ this.getMeta().hashCode());
            }
            return rc;
        }
        
        public Bean mergeFrom(final Getter other) {
            this.copyCheck();
            if (other.hasKey()) {
                this.setKey(other.getKey());
            }
            if (other.hasType()) {
                this.setType(other.getType());
            }
            if (other.hasMeta()) {
                this.setMeta(other.getMeta());
            }
            return this;
        }
        
        public void clear() {
            this.clearKey();
            this.clearType();
            this.clearMeta();
        }
        
        public void readExternal(final DataInput in) throws IOException {
            assert this.frozen == null : "Modification not allowed after object has been fozen.  Try modifying a copy of this object.";
            this.bean = this;
            this.frozen = null;
            this.f_key = in.readLong();
            this.b_key = true;
            this.f_type = in.readInt();
            this.b_type = true;
            final int size = in.readInt();
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
            out.writeLong(this.bean.f_key);
            out.writeInt(this.bean.f_type);
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
        
        public boolean hasKey() {
            return this.bean().hasKey();
        }
        
        public long getKey() {
            return this.bean().getKey();
        }
        
        public boolean hasType() {
            return this.bean().hasType();
        }
        
        public int getType() {
            return this.bean().getType();
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
                if (this.bean.hasKey()) {
                    output.writeInt64(1, this.bean.getKey());
                }
                if (this.bean.hasType()) {
                    output.writeInt32(2, this.bean.getType());
                }
                if (this.bean.hasMeta()) {
                    output.writeBytes(3, this.bean.getMeta());
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
            if (this.hasKey()) {
                this.size += CodedOutputStream.computeInt64Size(1, this.getKey());
            }
            if (this.hasType()) {
                this.size += CodedOutputStream.computeInt32Size(2, this.getType());
            }
            if (this.hasMeta()) {
                this.size += CodedOutputStream.computeBytesSize(3, this.getMeta());
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
        boolean hasKey();
        
        long getKey();
        
        boolean hasType();
        
        int getType();
        
        boolean hasMeta();
        
        org.fusesource.hawtbuf.Buffer getMeta();
        
        Bean copy();
        
        Buffer freeze();
        
        StringBuilder toString(final StringBuilder p0, final String p1);
    }
}
