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

public class MessagePB implements PBMessageFactory<Bean, Buffer>
{
    public static final MessagePB FACTORY;
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
        FACTORY = new MessagePB();
        FRAMED_CODEC = new PBMessageFramedCodec((PBMessageFactory)MessagePB.FACTORY);
        UNFRAMED_CODEC = new PBMessageUnframedCodec((PBMessageFactory)MessagePB.FACTORY);
    }
    
    public static final class Bean implements Getter
    {
        Buffer frozen;
        Bean bean;
        private long f_messageKey;
        private boolean b_messageKey;
        private AsciiBuffer f_codec;
        private int f_size;
        private boolean b_size;
        private org.fusesource.hawtbuf.Buffer f_value;
        private long f_expiration;
        private boolean b_expiration;
        private int f_compression;
        private boolean b_compression;
        private org.fusesource.hawtbuf.Buffer f_directData;
        private org.fusesource.hawtbuf.Buffer f_directFile;
        private long f_directOffset;
        private boolean b_directOffset;
        private int f_directSize;
        private boolean b_directSize;
        
        public Bean() {
            this.f_messageKey = 0L;
            this.f_codec = null;
            this.f_size = 0;
            this.f_value = null;
            this.f_expiration = 0L;
            this.f_compression = 0;
            this.f_directData = null;
            this.f_directFile = null;
            this.f_directOffset = 0L;
            this.f_directSize = 0;
            this.bean = this;
        }
        
        public Bean(final Bean copy) {
            this.f_messageKey = 0L;
            this.f_codec = null;
            this.f_size = 0;
            this.f_value = null;
            this.f_expiration = 0L;
            this.f_compression = 0;
            this.f_directData = null;
            this.f_directFile = null;
            this.f_directOffset = 0L;
            this.f_directSize = 0;
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
            this.f_messageKey = other.f_messageKey;
            this.b_messageKey = other.b_messageKey;
            this.f_codec = other.f_codec;
            this.f_size = other.f_size;
            this.b_size = other.b_size;
            this.f_value = other.f_value;
            this.f_expiration = other.f_expiration;
            this.b_expiration = other.b_expiration;
            this.f_compression = other.f_compression;
            this.b_compression = other.b_compression;
            this.f_directData = other.f_directData;
            this.f_directFile = other.f_directFile;
            this.f_directOffset = other.f_directOffset;
            this.b_directOffset = other.b_directOffset;
            this.f_directSize = other.f_directSize;
            this.b_directSize = other.b_directSize;
        }
        
        @Override
        public boolean hasMessageKey() {
            return this.bean.b_messageKey;
        }
        
        @Override
        public long getMessageKey() {
            return this.bean.f_messageKey;
        }
        
        public Bean setMessageKey(final long messageKey) {
            this.copyCheck();
            this.b_messageKey = true;
            this.f_messageKey = messageKey;
            return this;
        }
        
        public void clearMessageKey() {
            this.copyCheck();
            this.b_messageKey = false;
            this.f_messageKey = 0L;
        }
        
        @Override
        public boolean hasCodec() {
            return this.bean.f_codec != null;
        }
        
        @Override
        public AsciiBuffer getCodec() {
            return this.bean.f_codec;
        }
        
        public Bean setCodec(final AsciiBuffer codec) {
            this.copyCheck();
            this.f_codec = codec;
            return this;
        }
        
        public void clearCodec() {
            this.copyCheck();
            this.f_codec = null;
        }
        
        @Override
        public boolean hasSize() {
            return this.bean.b_size;
        }
        
        @Override
        public int getSize() {
            return this.bean.f_size;
        }
        
        public Bean setSize(final int size) {
            this.copyCheck();
            this.b_size = true;
            this.f_size = size;
            return this;
        }
        
        public void clearSize() {
            this.copyCheck();
            this.b_size = false;
            this.f_size = 0;
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
        public boolean hasExpiration() {
            return this.bean.b_expiration;
        }
        
        @Override
        public long getExpiration() {
            return this.bean.f_expiration;
        }
        
        public Bean setExpiration(final long expiration) {
            this.copyCheck();
            this.b_expiration = true;
            this.f_expiration = expiration;
            return this;
        }
        
        public void clearExpiration() {
            this.copyCheck();
            this.b_expiration = false;
            this.f_expiration = 0L;
        }
        
        @Override
        public boolean hasCompression() {
            return this.bean.b_compression;
        }
        
        @Override
        public int getCompression() {
            return this.bean.f_compression;
        }
        
        public Bean setCompression(final int compression) {
            this.copyCheck();
            this.b_compression = true;
            this.f_compression = compression;
            return this;
        }
        
        public void clearCompression() {
            this.copyCheck();
            this.b_compression = false;
            this.f_compression = 0;
        }
        
        @Override
        public boolean hasDirectData() {
            return this.bean.f_directData != null;
        }
        
        @Override
        public org.fusesource.hawtbuf.Buffer getDirectData() {
            return this.bean.f_directData;
        }
        
        public Bean setDirectData(final org.fusesource.hawtbuf.Buffer directData) {
            this.copyCheck();
            this.f_directData = directData;
            return this;
        }
        
        public void clearDirectData() {
            this.copyCheck();
            this.f_directData = null;
        }
        
        @Override
        public boolean hasDirectFile() {
            return this.bean.f_directFile != null;
        }
        
        @Override
        public org.fusesource.hawtbuf.Buffer getDirectFile() {
            return this.bean.f_directFile;
        }
        
        public Bean setDirectFile(final org.fusesource.hawtbuf.Buffer directFile) {
            this.copyCheck();
            this.f_directFile = directFile;
            return this;
        }
        
        public void clearDirectFile() {
            this.copyCheck();
            this.f_directFile = null;
        }
        
        @Override
        public boolean hasDirectOffset() {
            return this.bean.b_directOffset;
        }
        
        @Override
        public long getDirectOffset() {
            return this.bean.f_directOffset;
        }
        
        public Bean setDirectOffset(final long directOffset) {
            this.copyCheck();
            this.b_directOffset = true;
            this.f_directOffset = directOffset;
            return this;
        }
        
        public void clearDirectOffset() {
            this.copyCheck();
            this.b_directOffset = false;
            this.f_directOffset = 0L;
        }
        
        @Override
        public boolean hasDirectSize() {
            return this.bean.b_directSize;
        }
        
        @Override
        public int getDirectSize() {
            return this.bean.f_directSize;
        }
        
        public Bean setDirectSize(final int directSize) {
            this.copyCheck();
            this.b_directSize = true;
            this.f_directSize = directSize;
            return this;
        }
        
        public void clearDirectSize() {
            this.copyCheck();
            this.b_directSize = false;
            this.f_directSize = 0;
        }
        
        @Override
        public String toString() {
            return this.toString(new StringBuilder(), "").toString();
        }
        
        @Override
        public StringBuilder toString(final StringBuilder sb, final String prefix) {
            if (this.hasMessageKey()) {
                sb.append(prefix + "messageKey: ");
                sb.append(this.getMessageKey());
                sb.append("\n");
            }
            if (this.hasCodec()) {
                sb.append(prefix + "codec: ");
                sb.append(this.getCodec());
                sb.append("\n");
            }
            if (this.hasSize()) {
                sb.append(prefix + "size: ");
                sb.append(this.getSize());
                sb.append("\n");
            }
            if (this.hasValue()) {
                sb.append(prefix + "value: ");
                sb.append(this.getValue());
                sb.append("\n");
            }
            if (this.hasExpiration()) {
                sb.append(prefix + "expiration: ");
                sb.append(this.getExpiration());
                sb.append("\n");
            }
            if (this.hasCompression()) {
                sb.append(prefix + "compression: ");
                sb.append(this.getCompression());
                sb.append("\n");
            }
            if (this.hasDirectData()) {
                sb.append(prefix + "direct_data: ");
                sb.append(this.getDirectData());
                sb.append("\n");
            }
            if (this.hasDirectFile()) {
                sb.append(prefix + "direct_file: ");
                sb.append(this.getDirectFile());
                sb.append("\n");
            }
            if (this.hasDirectOffset()) {
                sb.append(prefix + "direct_offset: ");
                sb.append(this.getDirectOffset());
                sb.append("\n");
            }
            if (this.hasDirectSize()) {
                sb.append(prefix + "direct_size: ");
                sb.append(this.getDirectSize());
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
                        this.setMessageKey(input.readInt64());
                        continue;
                    }
                    case 18: {
                        this.setCodec(new AsciiBuffer(input.readBytes()));
                        continue;
                    }
                    case 24: {
                        this.setSize(input.readInt32());
                        continue;
                    }
                    case 34: {
                        this.setValue(input.readBytes());
                        continue;
                    }
                    case 40: {
                        this.setExpiration(input.readSInt64());
                        continue;
                    }
                    case 48: {
                        this.setCompression(input.readInt32());
                        continue;
                    }
                    case 82: {
                        this.setDirectData(input.readBytes());
                        continue;
                    }
                    case 98: {
                        this.setDirectFile(input.readBytes());
                        continue;
                    }
                    case 104: {
                        this.setDirectOffset(input.readInt64());
                        continue;
                    }
                    case 112: {
                        this.setDirectSize(input.readInt32());
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
            return !(this.hasMessageKey() ^ obj.hasMessageKey()) && (!this.hasMessageKey() || this.getMessageKey() == obj.getMessageKey()) && !(this.hasCodec() ^ obj.hasCodec()) && (!this.hasCodec() || this.getCodec().equals(obj.getCodec())) && !(this.hasSize() ^ obj.hasSize()) && (!this.hasSize() || this.getSize() == obj.getSize()) && !(this.hasValue() ^ obj.hasValue()) && (!this.hasValue() || this.getValue().equals(obj.getValue())) && !(this.hasExpiration() ^ obj.hasExpiration()) && (!this.hasExpiration() || this.getExpiration() == obj.getExpiration()) && !(this.hasCompression() ^ obj.hasCompression()) && (!this.hasCompression() || this.getCompression() == obj.getCompression()) && !(this.hasDirectData() ^ obj.hasDirectData()) && (!this.hasDirectData() || this.getDirectData().equals(obj.getDirectData())) && !(this.hasDirectFile() ^ obj.hasDirectFile()) && (!this.hasDirectFile() || this.getDirectFile().equals(obj.getDirectFile())) && !(this.hasDirectOffset() ^ obj.hasDirectOffset()) && (!this.hasDirectOffset() || this.getDirectOffset() == obj.getDirectOffset()) && !(this.hasDirectSize() ^ obj.hasDirectSize()) && (!this.hasDirectSize() || this.getDirectSize() == obj.getDirectSize());
        }
        
        @Override
        public int hashCode() {
            int rc = 2066384;
            if (this.hasMessageKey()) {
                rc ^= (0x120B30F8 ^ new Long(this.getMessageKey()).hashCode());
            }
            if (this.hasCodec()) {
                rc ^= (0x3E41FF6 ^ this.getCodec().hashCode());
            }
            if (this.hasSize()) {
                rc ^= (0x275421 ^ this.getSize());
            }
            if (this.hasValue()) {
                rc ^= (0x4E9A151 ^ this.getValue().hashCode());
            }
            if (this.hasExpiration()) {
                rc ^= (0x44E726CF ^ new Long(this.getExpiration()).hashCode());
            }
            if (this.hasCompression()) {
                rc ^= (0xB8C40146 ^ this.getCompression());
            }
            if (this.hasDirectData()) {
                rc ^= (0x8421BF53 ^ this.getDirectData().hashCode());
            }
            if (this.hasDirectFile()) {
                rc ^= (0x8422C525 ^ this.getDirectFile().hashCode());
            }
            if (this.hasDirectOffset()) {
                rc ^= (0x15B4EDFC ^ new Long(this.getDirectOffset()).hashCode());
            }
            if (this.hasDirectSize()) {
                rc ^= (0x8428AFAA ^ this.getDirectSize());
            }
            return rc;
        }
        
        public Bean mergeFrom(final Getter other) {
            this.copyCheck();
            if (other.hasMessageKey()) {
                this.setMessageKey(other.getMessageKey());
            }
            if (other.hasCodec()) {
                this.setCodec(other.getCodec());
            }
            if (other.hasSize()) {
                this.setSize(other.getSize());
            }
            if (other.hasValue()) {
                this.setValue(other.getValue());
            }
            if (other.hasExpiration()) {
                this.setExpiration(other.getExpiration());
            }
            if (other.hasCompression()) {
                this.setCompression(other.getCompression());
            }
            if (other.hasDirectData()) {
                this.setDirectData(other.getDirectData());
            }
            if (other.hasDirectFile()) {
                this.setDirectFile(other.getDirectFile());
            }
            if (other.hasDirectOffset()) {
                this.setDirectOffset(other.getDirectOffset());
            }
            if (other.hasDirectSize()) {
                this.setDirectSize(other.getDirectSize());
            }
            return this;
        }
        
        public void clear() {
            this.clearMessageKey();
            this.clearCodec();
            this.clearSize();
            this.clearValue();
            this.clearExpiration();
            this.clearCompression();
            this.clearDirectData();
            this.clearDirectFile();
            this.clearDirectOffset();
            this.clearDirectSize();
        }
        
        public void readExternal(final DataInput in) throws IOException {
            assert this.frozen == null : "Modification not allowed after object has been fozen.  Try modifying a copy of this object.";
            this.bean = this;
            this.frozen = null;
            this.f_messageKey = in.readLong();
            this.b_messageKey = true;
            int size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_codec = new AsciiBuffer(b);
            }
            else {
                this.f_codec = null;
            }
            this.f_size = in.readInt();
            this.b_size = true;
            size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_value = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_value = null;
            }
            this.f_expiration = in.readLong();
            this.b_expiration = true;
            this.f_compression = in.readInt();
            this.b_compression = true;
            size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_directData = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_directData = null;
            }
            size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_directFile = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_directFile = null;
            }
            this.f_directOffset = in.readLong();
            this.b_directOffset = true;
            this.f_directSize = in.readInt();
            this.b_directSize = true;
        }
        
        public void writeExternal(final DataOutput out) throws IOException {
            out.writeLong(this.bean.f_messageKey);
            if (this.bean.f_codec != null) {
                out.writeInt(this.bean.f_codec.getLength());
                out.write(this.bean.f_codec.getData(), this.bean.f_codec.getOffset(), this.bean.f_codec.getLength());
            }
            else {
                out.writeInt(-1);
            }
            out.writeInt(this.bean.f_size);
            if (this.bean.f_value != null) {
                out.writeInt(this.bean.f_value.getLength());
                out.write(this.bean.f_value.getData(), this.bean.f_value.getOffset(), this.bean.f_value.getLength());
            }
            else {
                out.writeInt(-1);
            }
            out.writeLong(this.bean.f_expiration);
            out.writeInt(this.bean.f_compression);
            if (this.bean.f_directData != null) {
                out.writeInt(this.bean.f_directData.getLength());
                out.write(this.bean.f_directData.getData(), this.bean.f_directData.getOffset(), this.bean.f_directData.getLength());
            }
            else {
                out.writeInt(-1);
            }
            if (this.bean.f_directFile != null) {
                out.writeInt(this.bean.f_directFile.getLength());
                out.write(this.bean.f_directFile.getData(), this.bean.f_directFile.getOffset(), this.bean.f_directFile.getLength());
            }
            else {
                out.writeInt(-1);
            }
            out.writeLong(this.bean.f_directOffset);
            out.writeInt(this.bean.f_directSize);
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
        
        public boolean hasMessageKey() {
            return this.bean().hasMessageKey();
        }
        
        public long getMessageKey() {
            return this.bean().getMessageKey();
        }
        
        public boolean hasCodec() {
            return this.bean().hasCodec();
        }
        
        public AsciiBuffer getCodec() {
            return this.bean().getCodec();
        }
        
        public boolean hasSize() {
            return this.bean().hasSize();
        }
        
        public int getSize() {
            return this.bean().getSize();
        }
        
        public boolean hasValue() {
            return this.bean().hasValue();
        }
        
        public org.fusesource.hawtbuf.Buffer getValue() {
            return this.bean().getValue();
        }
        
        public boolean hasExpiration() {
            return this.bean().hasExpiration();
        }
        
        public long getExpiration() {
            return this.bean().getExpiration();
        }
        
        public boolean hasCompression() {
            return this.bean().hasCompression();
        }
        
        public int getCompression() {
            return this.bean().getCompression();
        }
        
        public boolean hasDirectData() {
            return this.bean().hasDirectData();
        }
        
        public org.fusesource.hawtbuf.Buffer getDirectData() {
            return this.bean().getDirectData();
        }
        
        public boolean hasDirectFile() {
            return this.bean().hasDirectFile();
        }
        
        public org.fusesource.hawtbuf.Buffer getDirectFile() {
            return this.bean().getDirectFile();
        }
        
        public boolean hasDirectOffset() {
            return this.bean().hasDirectOffset();
        }
        
        public long getDirectOffset() {
            return this.bean().getDirectOffset();
        }
        
        public boolean hasDirectSize() {
            return this.bean().hasDirectSize();
        }
        
        public int getDirectSize() {
            return this.bean().getDirectSize();
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
                output.writeInt64(1, this.bean.getMessageKey());
                output.writeBytes(2, (org.fusesource.hawtbuf.Buffer)this.bean.getCodec());
                if (this.bean.hasSize()) {
                    output.writeInt32(3, this.bean.getSize());
                }
                if (this.bean.hasValue()) {
                    output.writeBytes(4, this.bean.getValue());
                }
                if (this.bean.hasExpiration()) {
                    output.writeSInt64(5, this.bean.getExpiration());
                }
                if (this.bean.hasCompression()) {
                    output.writeInt32(6, this.bean.getCompression());
                }
                if (this.bean.hasDirectData()) {
                    output.writeBytes(10, this.bean.getDirectData());
                }
                if (this.bean.hasDirectFile()) {
                    output.writeBytes(12, this.bean.getDirectFile());
                }
                if (this.bean.hasDirectOffset()) {
                    output.writeInt64(13, this.bean.getDirectOffset());
                }
                if (this.bean.hasDirectSize()) {
                    output.writeInt32(14, this.bean.getDirectSize());
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
            this.size += CodedOutputStream.computeInt64Size(1, this.getMessageKey());
            this.size += CodedOutputStream.computeBytesSize(2, (org.fusesource.hawtbuf.Buffer)this.getCodec());
            if (this.hasSize()) {
                this.size += CodedOutputStream.computeInt32Size(3, this.getSize());
            }
            if (this.hasValue()) {
                this.size += CodedOutputStream.computeBytesSize(4, this.getValue());
            }
            if (this.hasExpiration()) {
                this.size += CodedOutputStream.computeSInt64Size(5, this.getExpiration());
            }
            if (this.hasCompression()) {
                this.size += CodedOutputStream.computeInt32Size(6, this.getCompression());
            }
            if (this.hasDirectData()) {
                this.size += CodedOutputStream.computeBytesSize(10, this.getDirectData());
            }
            if (this.hasDirectFile()) {
                this.size += CodedOutputStream.computeBytesSize(12, this.getDirectFile());
            }
            if (this.hasDirectOffset()) {
                this.size += CodedOutputStream.computeInt64Size(13, this.getDirectOffset());
            }
            if (this.hasDirectSize()) {
                this.size += CodedOutputStream.computeInt32Size(14, this.getDirectSize());
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
        boolean hasMessageKey();
        
        long getMessageKey();
        
        boolean hasCodec();
        
        AsciiBuffer getCodec();
        
        boolean hasSize();
        
        int getSize();
        
        boolean hasValue();
        
        org.fusesource.hawtbuf.Buffer getValue();
        
        boolean hasExpiration();
        
        long getExpiration();
        
        boolean hasCompression();
        
        int getCompression();
        
        boolean hasDirectData();
        
        org.fusesource.hawtbuf.Buffer getDirectData();
        
        boolean hasDirectFile();
        
        org.fusesource.hawtbuf.Buffer getDirectFile();
        
        boolean hasDirectOffset();
        
        long getDirectOffset();
        
        boolean hasDirectSize();
        
        int getDirectSize();
        
        Bean copy();
        
        Buffer freeze();
        
        StringBuilder toString(final StringBuilder p0, final String p1);
    }
}
