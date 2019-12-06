// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command.store.proto;

import java.io.OutputStream;
import org.fusesource.hawtbuf.proto.CodedOutputStream;
import java.io.DataOutput;
import java.io.DataInput;
import org.fusesource.hawtbuf.AsciiBuffer;
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

public class QueuePB implements PBMessageFactory<Bean, Buffer>
{
    public static final QueuePB FACTORY;
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
        FACTORY = new QueuePB();
        FRAMED_CODEC = new PBMessageFramedCodec((PBMessageFactory)QueuePB.FACTORY);
        UNFRAMED_CODEC = new PBMessageUnframedCodec((PBMessageFactory)QueuePB.FACTORY);
    }
    
    public static final class Bean implements Getter
    {
        Buffer frozen;
        Bean bean;
        private long f_key;
        private boolean b_key;
        private AsciiBuffer f_bindingKind;
        private org.fusesource.hawtbuf.Buffer f_bindingData;
        
        public Bean() {
            this.f_key = 0L;
            this.f_bindingKind = null;
            this.f_bindingData = null;
            this.bean = this;
        }
        
        public Bean(final Bean copy) {
            this.f_key = 0L;
            this.f_bindingKind = null;
            this.f_bindingData = null;
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
            this.f_bindingKind = other.f_bindingKind;
            this.f_bindingData = other.f_bindingData;
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
        public boolean hasBindingKind() {
            return this.bean.f_bindingKind != null;
        }
        
        @Override
        public AsciiBuffer getBindingKind() {
            return this.bean.f_bindingKind;
        }
        
        public Bean setBindingKind(final AsciiBuffer bindingKind) {
            this.copyCheck();
            this.f_bindingKind = bindingKind;
            return this;
        }
        
        public void clearBindingKind() {
            this.copyCheck();
            this.f_bindingKind = null;
        }
        
        @Override
        public boolean hasBindingData() {
            return this.bean.f_bindingData != null;
        }
        
        @Override
        public org.fusesource.hawtbuf.Buffer getBindingData() {
            return this.bean.f_bindingData;
        }
        
        public Bean setBindingData(final org.fusesource.hawtbuf.Buffer bindingData) {
            this.copyCheck();
            this.f_bindingData = bindingData;
            return this;
        }
        
        public void clearBindingData() {
            this.copyCheck();
            this.f_bindingData = null;
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
            if (this.hasBindingKind()) {
                sb.append(prefix + "binding_kind: ");
                sb.append(this.getBindingKind());
                sb.append("\n");
            }
            if (this.hasBindingData()) {
                sb.append(prefix + "binding_data: ");
                sb.append(this.getBindingData());
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
                    case 18: {
                        this.setBindingKind(new AsciiBuffer(input.readBytes()));
                        continue;
                    }
                    case 26: {
                        this.setBindingData(input.readBytes());
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
            return !(this.hasKey() ^ obj.hasKey()) && (!this.hasKey() || this.getKey() == obj.getKey()) && !(this.hasBindingKind() ^ obj.hasBindingKind()) && (!this.hasBindingKind() || this.getBindingKind().equals(obj.getBindingKind())) && !(this.hasBindingData() ^ obj.hasBindingData()) && (!this.hasBindingData() || this.getBindingData().equals(obj.getBindingData()));
        }
        
        @Override
        public int hashCode() {
            int rc = 2066384;
            if (this.hasKey()) {
                rc ^= (0x1263F ^ new Long(this.getKey()).hashCode());
            }
            if (this.hasBindingKind()) {
                rc ^= (0x52653B99 ^ this.getBindingKind().hashCode());
            }
            if (this.hasBindingData()) {
                rc ^= (0x5261EFAF ^ this.getBindingData().hashCode());
            }
            return rc;
        }
        
        public Bean mergeFrom(final Getter other) {
            this.copyCheck();
            if (other.hasKey()) {
                this.setKey(other.getKey());
            }
            if (other.hasBindingKind()) {
                this.setBindingKind(other.getBindingKind());
            }
            if (other.hasBindingData()) {
                this.setBindingData(other.getBindingData());
            }
            return this;
        }
        
        public void clear() {
            this.clearKey();
            this.clearBindingKind();
            this.clearBindingData();
        }
        
        public void readExternal(final DataInput in) throws IOException {
            assert this.frozen == null : "Modification not allowed after object has been fozen.  Try modifying a copy of this object.";
            this.bean = this;
            this.frozen = null;
            this.f_key = in.readLong();
            this.b_key = true;
            int size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_bindingKind = new AsciiBuffer(b);
            }
            else {
                this.f_bindingKind = null;
            }
            size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_bindingData = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_bindingData = null;
            }
        }
        
        public void writeExternal(final DataOutput out) throws IOException {
            out.writeLong(this.bean.f_key);
            if (this.bean.f_bindingKind != null) {
                out.writeInt(this.bean.f_bindingKind.getLength());
                out.write(this.bean.f_bindingKind.getData(), this.bean.f_bindingKind.getOffset(), this.bean.f_bindingKind.getLength());
            }
            else {
                out.writeInt(-1);
            }
            if (this.bean.f_bindingData != null) {
                out.writeInt(this.bean.f_bindingData.getLength());
                out.write(this.bean.f_bindingData.getData(), this.bean.f_bindingData.getOffset(), this.bean.f_bindingData.getLength());
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
        
        public boolean hasBindingKind() {
            return this.bean().hasBindingKind();
        }
        
        public AsciiBuffer getBindingKind() {
            return this.bean().getBindingKind();
        }
        
        public boolean hasBindingData() {
            return this.bean().hasBindingData();
        }
        
        public org.fusesource.hawtbuf.Buffer getBindingData() {
            return this.bean().getBindingData();
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
                output.writeInt64(1, this.bean.getKey());
                if (this.bean.hasBindingKind()) {
                    output.writeBytes(2, (org.fusesource.hawtbuf.Buffer)this.bean.getBindingKind());
                }
                if (this.bean.hasBindingData()) {
                    output.writeBytes(3, this.bean.getBindingData());
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
            this.size += CodedOutputStream.computeInt64Size(1, this.getKey());
            if (this.hasBindingKind()) {
                this.size += CodedOutputStream.computeBytesSize(2, (org.fusesource.hawtbuf.Buffer)this.getBindingKind());
            }
            if (this.hasBindingData()) {
                this.size += CodedOutputStream.computeBytesSize(3, this.getBindingData());
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
        
        boolean hasBindingKind();
        
        AsciiBuffer getBindingKind();
        
        boolean hasBindingData();
        
        org.fusesource.hawtbuf.Buffer getBindingData();
        
        Bean copy();
        
        Buffer freeze();
        
        StringBuilder toString(final StringBuilder p0, final String p1);
    }
}
