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

public class SubscriptionRecord implements PBMessageFactory<Bean, Buffer>
{
    public static final SubscriptionRecord FACTORY;
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
        FACTORY = new SubscriptionRecord();
        FRAMED_CODEC = new PBMessageFramedCodec((PBMessageFactory)SubscriptionRecord.FACTORY);
        UNFRAMED_CODEC = new PBMessageUnframedCodec((PBMessageFactory)SubscriptionRecord.FACTORY);
    }
    
    public static final class Bean implements Getter
    {
        Buffer frozen;
        Bean bean;
        private long f_topicKey;
        private boolean b_topicKey;
        private String f_clientId;
        private String f_subscriptionName;
        private String f_selector;
        private String f_destinationName;
        
        public Bean() {
            this.f_topicKey = 0L;
            this.f_clientId = null;
            this.f_subscriptionName = null;
            this.f_selector = null;
            this.f_destinationName = null;
            this.bean = this;
        }
        
        public Bean(final Bean copy) {
            this.f_topicKey = 0L;
            this.f_clientId = null;
            this.f_subscriptionName = null;
            this.f_selector = null;
            this.f_destinationName = null;
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
            this.f_topicKey = other.f_topicKey;
            this.b_topicKey = other.b_topicKey;
            this.f_clientId = other.f_clientId;
            this.f_subscriptionName = other.f_subscriptionName;
            this.f_selector = other.f_selector;
            this.f_destinationName = other.f_destinationName;
        }
        
        @Override
        public boolean hasTopicKey() {
            return this.bean.b_topicKey;
        }
        
        @Override
        public long getTopicKey() {
            return this.bean.f_topicKey;
        }
        
        public Bean setTopicKey(final long topicKey) {
            this.copyCheck();
            this.b_topicKey = true;
            this.f_topicKey = topicKey;
            return this;
        }
        
        public void clearTopicKey() {
            this.copyCheck();
            this.b_topicKey = false;
            this.f_topicKey = 0L;
        }
        
        @Override
        public boolean hasClientId() {
            return this.bean.f_clientId != null;
        }
        
        @Override
        public String getClientId() {
            return this.bean.f_clientId;
        }
        
        public Bean setClientId(final String clientId) {
            this.copyCheck();
            this.f_clientId = clientId;
            return this;
        }
        
        public void clearClientId() {
            this.copyCheck();
            this.f_clientId = null;
        }
        
        @Override
        public boolean hasSubscriptionName() {
            return this.bean.f_subscriptionName != null;
        }
        
        @Override
        public String getSubscriptionName() {
            return this.bean.f_subscriptionName;
        }
        
        public Bean setSubscriptionName(final String subscriptionName) {
            this.copyCheck();
            this.f_subscriptionName = subscriptionName;
            return this;
        }
        
        public void clearSubscriptionName() {
            this.copyCheck();
            this.f_subscriptionName = null;
        }
        
        @Override
        public boolean hasSelector() {
            return this.bean.f_selector != null;
        }
        
        @Override
        public String getSelector() {
            return this.bean.f_selector;
        }
        
        public Bean setSelector(final String selector) {
            this.copyCheck();
            this.f_selector = selector;
            return this;
        }
        
        public void clearSelector() {
            this.copyCheck();
            this.f_selector = null;
        }
        
        @Override
        public boolean hasDestinationName() {
            return this.bean.f_destinationName != null;
        }
        
        @Override
        public String getDestinationName() {
            return this.bean.f_destinationName;
        }
        
        public Bean setDestinationName(final String destinationName) {
            this.copyCheck();
            this.f_destinationName = destinationName;
            return this;
        }
        
        public void clearDestinationName() {
            this.copyCheck();
            this.f_destinationName = null;
        }
        
        @Override
        public String toString() {
            return this.toString(new StringBuilder(), "").toString();
        }
        
        @Override
        public StringBuilder toString(final StringBuilder sb, final String prefix) {
            if (this.hasTopicKey()) {
                sb.append(prefix + "topic_key: ");
                sb.append(this.getTopicKey());
                sb.append("\n");
            }
            if (this.hasClientId()) {
                sb.append(prefix + "client_id: ");
                sb.append(this.getClientId());
                sb.append("\n");
            }
            if (this.hasSubscriptionName()) {
                sb.append(prefix + "subscription_name: ");
                sb.append(this.getSubscriptionName());
                sb.append("\n");
            }
            if (this.hasSelector()) {
                sb.append(prefix + "selector: ");
                sb.append(this.getSelector());
                sb.append("\n");
            }
            if (this.hasDestinationName()) {
                sb.append(prefix + "destination_name: ");
                sb.append(this.getDestinationName());
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
                        this.setTopicKey(input.readInt64());
                        continue;
                    }
                    case 18: {
                        this.setClientId(input.readString());
                        continue;
                    }
                    case 26: {
                        this.setSubscriptionName(input.readString());
                        continue;
                    }
                    case 34: {
                        this.setSelector(input.readString());
                        continue;
                    }
                    case 42: {
                        this.setDestinationName(input.readString());
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
            return !(this.hasTopicKey() ^ obj.hasTopicKey()) && (!this.hasTopicKey() || this.getTopicKey() == obj.getTopicKey()) && !(this.hasClientId() ^ obj.hasClientId()) && (!this.hasClientId() || this.getClientId().equals(obj.getClientId())) && !(this.hasSubscriptionName() ^ obj.hasSubscriptionName()) && (!this.hasSubscriptionName() || this.getSubscriptionName().equals(obj.getSubscriptionName())) && !(this.hasSelector() ^ obj.hasSelector()) && (!this.hasSelector() || this.getSelector().equals(obj.getSelector())) && !(this.hasDestinationName() ^ obj.hasDestinationName()) && (!this.hasDestinationName() || this.getDestinationName().equals(obj.getDestinationName()));
        }
        
        @Override
        public int hashCode() {
            int rc = 2066384;
            if (this.hasTopicKey()) {
                rc ^= (0xCACAFAD0 ^ new Long(this.getTopicKey()).hashCode());
            }
            if (this.hasClientId()) {
                rc ^= (0x39FF9A66 ^ this.getClientId().hashCode());
            }
            if (this.hasSubscriptionName()) {
                rc ^= (0xAF03B568 ^ this.getSubscriptionName().hashCode());
            }
            if (this.hasSelector()) {
                rc ^= (0x4AE057FF ^ this.getSelector().hashCode());
            }
            if (this.hasDestinationName()) {
                rc ^= (0x5B813399 ^ this.getDestinationName().hashCode());
            }
            return rc;
        }
        
        public Bean mergeFrom(final Getter other) {
            this.copyCheck();
            if (other.hasTopicKey()) {
                this.setTopicKey(other.getTopicKey());
            }
            if (other.hasClientId()) {
                this.setClientId(other.getClientId());
            }
            if (other.hasSubscriptionName()) {
                this.setSubscriptionName(other.getSubscriptionName());
            }
            if (other.hasSelector()) {
                this.setSelector(other.getSelector());
            }
            if (other.hasDestinationName()) {
                this.setDestinationName(other.getDestinationName());
            }
            return this;
        }
        
        public void clear() {
            this.clearTopicKey();
            this.clearClientId();
            this.clearSubscriptionName();
            this.clearSelector();
            this.clearDestinationName();
        }
        
        public void readExternal(final DataInput in) throws IOException {
            assert this.frozen == null : "Modification not allowed after object has been fozen.  Try modifying a copy of this object.";
            this.bean = this;
            this.frozen = null;
            this.f_topicKey = in.readLong();
            this.b_topicKey = true;
            if (in.readBoolean()) {
                this.f_clientId = in.readUTF();
            }
            else {
                this.f_clientId = null;
            }
            if (in.readBoolean()) {
                this.f_subscriptionName = in.readUTF();
            }
            else {
                this.f_subscriptionName = null;
            }
            if (in.readBoolean()) {
                this.f_selector = in.readUTF();
            }
            else {
                this.f_selector = null;
            }
            if (in.readBoolean()) {
                this.f_destinationName = in.readUTF();
            }
            else {
                this.f_destinationName = null;
            }
        }
        
        public void writeExternal(final DataOutput out) throws IOException {
            out.writeLong(this.bean.f_topicKey);
            if (this.bean.f_clientId != null) {
                out.writeBoolean(true);
                out.writeUTF(this.bean.f_clientId);
            }
            else {
                out.writeBoolean(false);
            }
            if (this.bean.f_subscriptionName != null) {
                out.writeBoolean(true);
                out.writeUTF(this.bean.f_subscriptionName);
            }
            else {
                out.writeBoolean(false);
            }
            if (this.bean.f_selector != null) {
                out.writeBoolean(true);
                out.writeUTF(this.bean.f_selector);
            }
            else {
                out.writeBoolean(false);
            }
            if (this.bean.f_destinationName != null) {
                out.writeBoolean(true);
                out.writeUTF(this.bean.f_destinationName);
            }
            else {
                out.writeBoolean(false);
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
        
        public boolean hasTopicKey() {
            return this.bean().hasTopicKey();
        }
        
        public long getTopicKey() {
            return this.bean().getTopicKey();
        }
        
        public boolean hasClientId() {
            return this.bean().hasClientId();
        }
        
        public String getClientId() {
            return this.bean().getClientId();
        }
        
        public boolean hasSubscriptionName() {
            return this.bean().hasSubscriptionName();
        }
        
        public String getSubscriptionName() {
            return this.bean().getSubscriptionName();
        }
        
        public boolean hasSelector() {
            return this.bean().hasSelector();
        }
        
        public String getSelector() {
            return this.bean().getSelector();
        }
        
        public boolean hasDestinationName() {
            return this.bean().hasDestinationName();
        }
        
        public String getDestinationName() {
            return this.bean().getDestinationName();
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
                if (this.bean.hasTopicKey()) {
                    output.writeInt64(1, this.bean.getTopicKey());
                }
                if (this.bean.hasClientId()) {
                    output.writeString(2, this.bean.getClientId());
                }
                if (this.bean.hasSubscriptionName()) {
                    output.writeString(3, this.bean.getSubscriptionName());
                }
                if (this.bean.hasSelector()) {
                    output.writeString(4, this.bean.getSelector());
                }
                if (this.bean.hasDestinationName()) {
                    output.writeString(5, this.bean.getDestinationName());
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
            if (this.hasTopicKey()) {
                this.size += CodedOutputStream.computeInt64Size(1, this.getTopicKey());
            }
            if (this.hasClientId()) {
                this.size += CodedOutputStream.computeStringSize(2, this.getClientId());
            }
            if (this.hasSubscriptionName()) {
                this.size += CodedOutputStream.computeStringSize(3, this.getSubscriptionName());
            }
            if (this.hasSelector()) {
                this.size += CodedOutputStream.computeStringSize(4, this.getSelector());
            }
            if (this.hasDestinationName()) {
                this.size += CodedOutputStream.computeStringSize(5, this.getDestinationName());
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
        boolean hasTopicKey();
        
        long getTopicKey();
        
        boolean hasClientId();
        
        String getClientId();
        
        boolean hasSubscriptionName();
        
        String getSubscriptionName();
        
        boolean hasSelector();
        
        String getSelector();
        
        boolean hasDestinationName();
        
        String getDestinationName();
        
        Bean copy();
        
        Buffer freeze();
        
        StringBuilder toString(final StringBuilder p0, final String p1);
    }
}
