// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command.store.proto;

import java.io.OutputStream;
import org.fusesource.hawtbuf.proto.CodedOutputStream;
import java.util.Iterator;
import java.io.DataOutput;
import java.io.DataInput;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
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

public class QueueEntryPB implements PBMessageFactory<Bean, Buffer>
{
    public static final QueueEntryPB FACTORY;
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
        FACTORY = new QueueEntryPB();
        FRAMED_CODEC = new PBMessageFramedCodec((PBMessageFactory)QueueEntryPB.FACTORY);
        UNFRAMED_CODEC = new PBMessageUnframedCodec((PBMessageFactory)QueueEntryPB.FACTORY);
    }
    
    public static final class Bean implements Getter
    {
        Buffer frozen;
        Bean bean;
        private long f_queueKey;
        private boolean b_queueKey;
        private long f_queueSeq;
        private boolean b_queueSeq;
        private long f_messageKey;
        private boolean b_messageKey;
        private int f_size;
        private boolean b_size;
        private org.fusesource.hawtbuf.Buffer f_attachment;
        private int f_redeliveries;
        private boolean b_redeliveries;
        private long f_expiration;
        private boolean b_expiration;
        private org.fusesource.hawtbuf.Buffer f_messageLocator;
        private List<org.fusesource.hawtbuf.Buffer> f_sender;
        
        public Bean() {
            this.f_queueKey = 0L;
            this.f_queueSeq = 0L;
            this.f_messageKey = 0L;
            this.f_size = 0;
            this.f_attachment = null;
            this.f_redeliveries = 0;
            this.f_expiration = 0L;
            this.f_messageLocator = null;
            this.bean = this;
        }
        
        public Bean(final Bean copy) {
            this.f_queueKey = 0L;
            this.f_queueSeq = 0L;
            this.f_messageKey = 0L;
            this.f_size = 0;
            this.f_attachment = null;
            this.f_redeliveries = 0;
            this.f_expiration = 0L;
            this.f_messageLocator = null;
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
            this.f_queueKey = other.f_queueKey;
            this.b_queueKey = other.b_queueKey;
            this.f_queueSeq = other.f_queueSeq;
            this.b_queueSeq = other.b_queueSeq;
            this.f_messageKey = other.f_messageKey;
            this.b_messageKey = other.b_messageKey;
            this.f_size = other.f_size;
            this.b_size = other.b_size;
            this.f_attachment = other.f_attachment;
            this.f_redeliveries = other.f_redeliveries;
            this.b_redeliveries = other.b_redeliveries;
            this.f_expiration = other.f_expiration;
            this.b_expiration = other.b_expiration;
            this.f_messageLocator = other.f_messageLocator;
            this.f_sender = other.f_sender;
            if (this.f_sender != null && !other.frozen()) {
                this.f_sender = new ArrayList<org.fusesource.hawtbuf.Buffer>(this.f_sender);
            }
        }
        
        @Override
        public boolean hasQueueKey() {
            return this.bean.b_queueKey;
        }
        
        @Override
        public long getQueueKey() {
            return this.bean.f_queueKey;
        }
        
        public Bean setQueueKey(final long queueKey) {
            this.copyCheck();
            this.b_queueKey = true;
            this.f_queueKey = queueKey;
            return this;
        }
        
        public void clearQueueKey() {
            this.copyCheck();
            this.b_queueKey = false;
            this.f_queueKey = 0L;
        }
        
        @Override
        public boolean hasQueueSeq() {
            return this.bean.b_queueSeq;
        }
        
        @Override
        public long getQueueSeq() {
            return this.bean.f_queueSeq;
        }
        
        public Bean setQueueSeq(final long queueSeq) {
            this.copyCheck();
            this.b_queueSeq = true;
            this.f_queueSeq = queueSeq;
            return this;
        }
        
        public void clearQueueSeq() {
            this.copyCheck();
            this.b_queueSeq = false;
            this.f_queueSeq = 0L;
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
        public boolean hasAttachment() {
            return this.bean.f_attachment != null;
        }
        
        @Override
        public org.fusesource.hawtbuf.Buffer getAttachment() {
            return this.bean.f_attachment;
        }
        
        public Bean setAttachment(final org.fusesource.hawtbuf.Buffer attachment) {
            this.copyCheck();
            this.f_attachment = attachment;
            return this;
        }
        
        public void clearAttachment() {
            this.copyCheck();
            this.f_attachment = null;
        }
        
        @Override
        public boolean hasRedeliveries() {
            return this.bean.b_redeliveries;
        }
        
        @Override
        public int getRedeliveries() {
            return this.bean.f_redeliveries;
        }
        
        public Bean setRedeliveries(final int redeliveries) {
            this.copyCheck();
            this.b_redeliveries = true;
            this.f_redeliveries = redeliveries;
            return this;
        }
        
        public void clearRedeliveries() {
            this.copyCheck();
            this.b_redeliveries = false;
            this.f_redeliveries = 0;
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
        public boolean hasMessageLocator() {
            return this.bean.f_messageLocator != null;
        }
        
        @Override
        public org.fusesource.hawtbuf.Buffer getMessageLocator() {
            return this.bean.f_messageLocator;
        }
        
        public Bean setMessageLocator(final org.fusesource.hawtbuf.Buffer messageLocator) {
            this.copyCheck();
            this.f_messageLocator = messageLocator;
            return this;
        }
        
        public void clearMessageLocator() {
            this.copyCheck();
            this.f_messageLocator = null;
        }
        
        @Override
        public boolean hasSender() {
            return this.bean.f_sender != null && !this.bean.f_sender.isEmpty();
        }
        
        @Override
        public List<org.fusesource.hawtbuf.Buffer> getSenderList() {
            return this.bean.f_sender;
        }
        
        public List<org.fusesource.hawtbuf.Buffer> createSenderList() {
            this.copyCheck();
            if (this.f_sender == null) {
                this.f_sender = new ArrayList<org.fusesource.hawtbuf.Buffer>();
            }
            return this.bean.f_sender;
        }
        
        public Bean setSenderList(final List<org.fusesource.hawtbuf.Buffer> sender) {
            this.copyCheck();
            this.f_sender = sender;
            return this;
        }
        
        @Override
        public int getSenderCount() {
            if (this.bean.f_sender == null) {
                return 0;
            }
            return this.bean.f_sender.size();
        }
        
        @Override
        public org.fusesource.hawtbuf.Buffer getSender(final int index) {
            if (this.bean.f_sender == null) {
                return null;
            }
            return this.bean.f_sender.get(index);
        }
        
        public Bean setSender(final int index, final org.fusesource.hawtbuf.Buffer value) {
            this.createSenderList().set(index, value);
            return this;
        }
        
        public Bean addSender(final org.fusesource.hawtbuf.Buffer value) {
            this.createSenderList().add(value);
            return this;
        }
        
        public Bean addAllSender(final Iterable<? extends org.fusesource.hawtbuf.Buffer> collection) {
            MessageBufferSupport.addAll((Iterable)collection, (Collection)this.createSenderList());
            return this;
        }
        
        public void clearSender() {
            this.copyCheck();
            this.f_sender = null;
        }
        
        @Override
        public String toString() {
            return this.toString(new StringBuilder(), "").toString();
        }
        
        @Override
        public StringBuilder toString(final StringBuilder sb, final String prefix) {
            if (this.hasQueueKey()) {
                sb.append(prefix + "queueKey: ");
                sb.append(this.getQueueKey());
                sb.append("\n");
            }
            if (this.hasQueueSeq()) {
                sb.append(prefix + "queueSeq: ");
                sb.append(this.getQueueSeq());
                sb.append("\n");
            }
            if (this.hasMessageKey()) {
                sb.append(prefix + "messageKey: ");
                sb.append(this.getMessageKey());
                sb.append("\n");
            }
            if (this.hasSize()) {
                sb.append(prefix + "size: ");
                sb.append(this.getSize());
                sb.append("\n");
            }
            if (this.hasAttachment()) {
                sb.append(prefix + "attachment: ");
                sb.append(this.getAttachment());
                sb.append("\n");
            }
            if (this.hasRedeliveries()) {
                sb.append(prefix + "redeliveries: ");
                sb.append(this.getRedeliveries());
                sb.append("\n");
            }
            if (this.hasExpiration()) {
                sb.append(prefix + "expiration: ");
                sb.append(this.getExpiration());
                sb.append("\n");
            }
            if (this.hasMessageLocator()) {
                sb.append(prefix + "messageLocator: ");
                sb.append(this.getMessageLocator());
                sb.append("\n");
            }
            if (this.hasSender()) {
                final List<org.fusesource.hawtbuf.Buffer> l = this.getSenderList();
                for (int i = 0; i < l.size(); ++i) {
                    sb.append(prefix + "sender[" + i + "]: ");
                    sb.append(l.get(i));
                    sb.append("\n");
                }
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
                        this.setQueueKey(input.readInt64());
                        continue;
                    }
                    case 16: {
                        this.setQueueSeq(input.readInt64());
                        continue;
                    }
                    case 24: {
                        this.setMessageKey(input.readInt64());
                        continue;
                    }
                    case 32: {
                        this.setSize(input.readInt32());
                        continue;
                    }
                    case 42: {
                        this.setAttachment(input.readBytes());
                        continue;
                    }
                    case 48: {
                        this.setRedeliveries(input.readInt32());
                        continue;
                    }
                    case 56: {
                        this.setExpiration(input.readSInt64());
                        continue;
                    }
                    case 66: {
                        this.setMessageLocator(input.readBytes());
                        continue;
                    }
                    case 74: {
                        this.createSenderList().add(input.readBytes());
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
            return !(this.hasQueueKey() ^ obj.hasQueueKey()) && (!this.hasQueueKey() || this.getQueueKey() == obj.getQueueKey()) && !(this.hasQueueSeq() ^ obj.hasQueueSeq()) && (!this.hasQueueSeq() || this.getQueueSeq() == obj.getQueueSeq()) && !(this.hasMessageKey() ^ obj.hasMessageKey()) && (!this.hasMessageKey() || this.getMessageKey() == obj.getMessageKey()) && !(this.hasSize() ^ obj.hasSize()) && (!this.hasSize() || this.getSize() == obj.getSize()) && !(this.hasAttachment() ^ obj.hasAttachment()) && (!this.hasAttachment() || this.getAttachment().equals(obj.getAttachment())) && !(this.hasRedeliveries() ^ obj.hasRedeliveries()) && (!this.hasRedeliveries() || this.getRedeliveries() == obj.getRedeliveries()) && !(this.hasExpiration() ^ obj.hasExpiration()) && (!this.hasExpiration() || this.getExpiration() == obj.getExpiration()) && !(this.hasMessageLocator() ^ obj.hasMessageLocator()) && (!this.hasMessageLocator() || this.getMessageLocator().equals(obj.getMessageLocator())) && !(this.hasSender() ^ obj.hasSender()) && (!this.hasSender() || this.getSenderList().equals(obj.getSenderList()));
        }
        
        @Override
        public int hashCode() {
            int rc = 2066384;
            if (this.hasQueueKey()) {
                rc ^= (0xBE718BAE ^ new Long(this.getQueueKey()).hashCode());
            }
            if (this.hasQueueSeq()) {
                rc ^= (0xBE71A9AE ^ new Long(this.getQueueSeq()).hashCode());
            }
            if (this.hasMessageKey()) {
                rc ^= (0x120B30F8 ^ new Long(this.getMessageKey()).hashCode());
            }
            if (this.hasSize()) {
                rc ^= (0x275421 ^ this.getSize());
            }
            if (this.hasAttachment()) {
                rc ^= (0x1C93543 ^ this.getAttachment().hashCode());
            }
            if (this.hasRedeliveries()) {
                rc ^= (0x755F3E25 ^ this.getRedeliveries());
            }
            if (this.hasExpiration()) {
                rc ^= (0x44E726CF ^ new Long(this.getExpiration()).hashCode());
            }
            if (this.hasMessageLocator()) {
                rc ^= (0xB887AAF ^ this.getMessageLocator().hashCode());
            }
            if (this.hasSender()) {
                rc ^= (0x93650655 ^ this.getSenderList().hashCode());
            }
            return rc;
        }
        
        public Bean mergeFrom(final Getter other) {
            this.copyCheck();
            if (other.hasQueueKey()) {
                this.setQueueKey(other.getQueueKey());
            }
            if (other.hasQueueSeq()) {
                this.setQueueSeq(other.getQueueSeq());
            }
            if (other.hasMessageKey()) {
                this.setMessageKey(other.getMessageKey());
            }
            if (other.hasSize()) {
                this.setSize(other.getSize());
            }
            if (other.hasAttachment()) {
                this.setAttachment(other.getAttachment());
            }
            if (other.hasRedeliveries()) {
                this.setRedeliveries(other.getRedeliveries());
            }
            if (other.hasExpiration()) {
                this.setExpiration(other.getExpiration());
            }
            if (other.hasMessageLocator()) {
                this.setMessageLocator(other.getMessageLocator());
            }
            if (other.hasSender()) {
                this.getSenderList().addAll(other.getSenderList());
            }
            return this;
        }
        
        public void clear() {
            this.clearQueueKey();
            this.clearQueueSeq();
            this.clearMessageKey();
            this.clearSize();
            this.clearAttachment();
            this.clearRedeliveries();
            this.clearExpiration();
            this.clearMessageLocator();
            this.clearSender();
        }
        
        public void readExternal(final DataInput in) throws IOException {
            assert this.frozen == null : "Modification not allowed after object has been fozen.  Try modifying a copy of this object.";
            this.bean = this;
            this.frozen = null;
            this.f_queueKey = in.readLong();
            this.b_queueKey = true;
            this.f_queueSeq = in.readLong();
            this.b_queueSeq = true;
            this.f_messageKey = in.readLong();
            this.b_messageKey = true;
            this.f_size = in.readInt();
            this.b_size = true;
            int size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_attachment = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_attachment = null;
            }
            this.f_redeliveries = in.readInt();
            this.b_redeliveries = true;
            this.f_expiration = in.readLong();
            this.b_expiration = true;
            size = in.readInt();
            if (size >= 0) {
                final byte[] b = new byte[size];
                in.readFully(b);
                this.f_messageLocator = new org.fusesource.hawtbuf.Buffer(b);
            }
            else {
                this.f_messageLocator = null;
            }
            size = in.readShort();
            if (size >= 0) {
                this.f_sender = new ArrayList<org.fusesource.hawtbuf.Buffer>(size);
                for (int i = 0; i < size; ++i) {
                    final byte[] b2 = new byte[in.readInt()];
                    in.readFully(b2);
                    this.f_sender.add(new org.fusesource.hawtbuf.Buffer(b2));
                }
            }
            else {
                this.f_sender = null;
            }
        }
        
        public void writeExternal(final DataOutput out) throws IOException {
            out.writeLong(this.bean.f_queueKey);
            out.writeLong(this.bean.f_queueSeq);
            out.writeLong(this.bean.f_messageKey);
            out.writeInt(this.bean.f_size);
            if (this.bean.f_attachment != null) {
                out.writeInt(this.bean.f_attachment.getLength());
                out.write(this.bean.f_attachment.getData(), this.bean.f_attachment.getOffset(), this.bean.f_attachment.getLength());
            }
            else {
                out.writeInt(-1);
            }
            out.writeInt(this.bean.f_redeliveries);
            out.writeLong(this.bean.f_expiration);
            if (this.bean.f_messageLocator != null) {
                out.writeInt(this.bean.f_messageLocator.getLength());
                out.write(this.bean.f_messageLocator.getData(), this.bean.f_messageLocator.getOffset(), this.bean.f_messageLocator.getLength());
            }
            else {
                out.writeInt(-1);
            }
            if (this.bean.f_sender != null) {
                out.writeShort(this.bean.f_sender.size());
                for (final org.fusesource.hawtbuf.Buffer o : this.bean.f_sender) {
                    out.writeInt(o.getLength());
                    out.write(o.getData(), o.getOffset(), o.getLength());
                }
            }
            else {
                out.writeShort(-1);
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
        
        public boolean hasQueueKey() {
            return this.bean().hasQueueKey();
        }
        
        public long getQueueKey() {
            return this.bean().getQueueKey();
        }
        
        public boolean hasQueueSeq() {
            return this.bean().hasQueueSeq();
        }
        
        public long getQueueSeq() {
            return this.bean().getQueueSeq();
        }
        
        public boolean hasMessageKey() {
            return this.bean().hasMessageKey();
        }
        
        public long getMessageKey() {
            return this.bean().getMessageKey();
        }
        
        public boolean hasSize() {
            return this.bean().hasSize();
        }
        
        public int getSize() {
            return this.bean().getSize();
        }
        
        public boolean hasAttachment() {
            return this.bean().hasAttachment();
        }
        
        public org.fusesource.hawtbuf.Buffer getAttachment() {
            return this.bean().getAttachment();
        }
        
        public boolean hasRedeliveries() {
            return this.bean().hasRedeliveries();
        }
        
        public int getRedeliveries() {
            return this.bean().getRedeliveries();
        }
        
        public boolean hasExpiration() {
            return this.bean().hasExpiration();
        }
        
        public long getExpiration() {
            return this.bean().getExpiration();
        }
        
        public boolean hasMessageLocator() {
            return this.bean().hasMessageLocator();
        }
        
        public org.fusesource.hawtbuf.Buffer getMessageLocator() {
            return this.bean().getMessageLocator();
        }
        
        public boolean hasSender() {
            return this.bean().hasSender();
        }
        
        public List<org.fusesource.hawtbuf.Buffer> getSenderList() {
            return this.bean().getSenderList();
        }
        
        public int getSenderCount() {
            return this.bean().getSenderCount();
        }
        
        public org.fusesource.hawtbuf.Buffer getSender(final int index) {
            return this.bean().getSender(index);
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
                output.writeInt64(1, this.bean.getQueueKey());
                output.writeInt64(2, this.bean.getQueueSeq());
                output.writeInt64(3, this.bean.getMessageKey());
                if (this.bean.hasSize()) {
                    output.writeInt32(4, this.bean.getSize());
                }
                if (this.bean.hasAttachment()) {
                    output.writeBytes(5, this.bean.getAttachment());
                }
                if (this.bean.hasRedeliveries()) {
                    output.writeInt32(6, this.bean.getRedeliveries());
                }
                if (this.bean.hasExpiration()) {
                    output.writeSInt64(7, this.bean.getExpiration());
                }
                if (this.bean.hasMessageLocator()) {
                    output.writeBytes(8, this.bean.getMessageLocator());
                }
                if (this.bean.hasSender()) {
                    for (final org.fusesource.hawtbuf.Buffer i : this.bean.getSenderList()) {
                        output.writeBytes(9, i);
                    }
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
            this.size += CodedOutputStream.computeInt64Size(1, this.getQueueKey());
            this.size += CodedOutputStream.computeInt64Size(2, this.getQueueSeq());
            this.size += CodedOutputStream.computeInt64Size(3, this.getMessageKey());
            if (this.hasSize()) {
                this.size += CodedOutputStream.computeInt32Size(4, this.getSize());
            }
            if (this.hasAttachment()) {
                this.size += CodedOutputStream.computeBytesSize(5, this.getAttachment());
            }
            if (this.hasRedeliveries()) {
                this.size += CodedOutputStream.computeInt32Size(6, this.getRedeliveries());
            }
            if (this.hasExpiration()) {
                this.size += CodedOutputStream.computeSInt64Size(7, this.getExpiration());
            }
            if (this.hasMessageLocator()) {
                this.size += CodedOutputStream.computeBytesSize(8, this.getMessageLocator());
            }
            if (this.hasSender()) {
                for (final org.fusesource.hawtbuf.Buffer i : this.getSenderList()) {
                    this.size += CodedOutputStream.computeBytesSize(9, i);
                }
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
        boolean hasQueueKey();
        
        long getQueueKey();
        
        boolean hasQueueSeq();
        
        long getQueueSeq();
        
        boolean hasMessageKey();
        
        long getMessageKey();
        
        boolean hasSize();
        
        int getSize();
        
        boolean hasAttachment();
        
        org.fusesource.hawtbuf.Buffer getAttachment();
        
        boolean hasRedeliveries();
        
        int getRedeliveries();
        
        boolean hasExpiration();
        
        long getExpiration();
        
        boolean hasMessageLocator();
        
        org.fusesource.hawtbuf.Buffer getMessageLocator();
        
        boolean hasSender();
        
        List<org.fusesource.hawtbuf.Buffer> getSenderList();
        
        int getSenderCount();
        
        org.fusesource.hawtbuf.Buffer getSender(final int p0);
        
        Bean copy();
        
        Buffer freeze();
        
        StringBuilder toString(final StringBuilder p0, final String p1);
    }
}
