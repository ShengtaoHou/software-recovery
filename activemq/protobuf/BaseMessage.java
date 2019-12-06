// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.util.Iterator;
import java.util.Collection;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseMessage<T> implements Message<T>
{
    protected int memoizedSerializedSize;
    
    public BaseMessage() {
        this.memoizedSerializedSize = -1;
    }
    
    public abstract T clone() throws CloneNotSupportedException;
    
    public void clear() {
        this.memoizedSerializedSize = -1;
    }
    
    public boolean isInitialized() {
        return this.missingFields().isEmpty();
    }
    
    public T assertInitialized() throws UninitializedMessageException {
        final ArrayList<String> missingFields = this.missingFields();
        if (!missingFields.isEmpty()) {
            throw new UninitializedMessageException(missingFields);
        }
        return this.getThis();
    }
    
    protected T checktInitialized() throws InvalidProtocolBufferException {
        final ArrayList<String> missingFields = this.missingFields();
        if (!missingFields.isEmpty()) {
            throw new UninitializedMessageException(missingFields).asInvalidProtocolBufferException();
        }
        return this.getThis();
    }
    
    public ArrayList<String> missingFields() {
        this.load();
        return new ArrayList<String>();
    }
    
    protected void loadAndClear() {
        this.memoizedSerializedSize = -1;
    }
    
    protected void load() {
    }
    
    public T mergeFrom(final T other) {
        return this.getThis();
    }
    
    public void writeUnframed(final CodedOutputStream output) throws IOException {
    }
    
    public void writeFramed(final CodedOutputStream output) throws IOException {
        output.writeRawVarint32(this.serializedSizeUnframed());
        this.writeUnframed(output);
    }
    
    public Buffer toUnframedBuffer() {
        try {
            final int size = this.serializedSizeUnframed();
            final BufferOutputStream baos = new BufferOutputStream(size);
            final CodedOutputStream output = new CodedOutputStream(baos);
            this.writeUnframed(output);
            final Buffer rc = baos.toBuffer();
            if (rc.length != size) {
                throw new IllegalStateException("Did not write as much data as expected.");
            }
            return rc;
        }
        catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }
    
    public Buffer toFramedBuffer() {
        try {
            final int size = this.serializedSizeFramed();
            final BufferOutputStream baos = new BufferOutputStream(size);
            final CodedOutputStream output = new CodedOutputStream(baos);
            this.writeFramed(output);
            final Buffer rc = baos.toBuffer();
            if (rc.length != size) {
                throw new IllegalStateException("Did not write as much data as expected.");
            }
            return rc;
        }
        catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }
    
    public byte[] toUnframedByteArray() {
        return this.toUnframedBuffer().toByteArray();
    }
    
    public byte[] toFramedByteArray() {
        return this.toFramedBuffer().toByteArray();
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
    
    public int serializedSizeFramed() {
        final int t = this.serializedSizeUnframed();
        return CodedOutputStream.computeRawVarint32Size(t) + t;
    }
    
    public T mergeFramed(final CodedInputStream input) throws IOException {
        final int length = input.readRawVarint32();
        final int oldLimit = input.pushLimit(length);
        final T rc = this.mergeUnframed(input);
        input.checkLastTagWas(0);
        input.popLimit(oldLimit);
        return rc;
    }
    
    public T mergeUnframed(final Buffer data) throws InvalidProtocolBufferException {
        try {
            final CodedInputStream input = new CodedInputStream(data);
            this.mergeUnframed(input);
            input.checkLastTagWas(0);
            return this.getThis();
        }
        catch (InvalidProtocolBufferException e) {
            throw e;
        }
        catch (IOException e2) {
            throw new RuntimeException("An IOException was thrown (should never happen in this method).", e2);
        }
    }
    
    private T getThis() {
        return (T)this;
    }
    
    public T mergeFramed(final Buffer data) throws InvalidProtocolBufferException {
        try {
            final CodedInputStream input = new CodedInputStream(data);
            this.mergeFramed(input);
            input.checkLastTagWas(0);
            return this.getThis();
        }
        catch (InvalidProtocolBufferException e) {
            throw e;
        }
        catch (IOException e2) {
            throw new RuntimeException("An IOException was thrown (should never happen in this method).", e2);
        }
    }
    
    public T mergeUnframed(final byte[] data) throws InvalidProtocolBufferException {
        return this.mergeUnframed(new Buffer(data));
    }
    
    public T mergeFramed(final byte[] data) throws InvalidProtocolBufferException {
        return this.mergeFramed(new Buffer(data));
    }
    
    public T mergeUnframed(final InputStream input) throws IOException {
        final CodedInputStream codedInput = new CodedInputStream(input);
        this.mergeUnframed(codedInput);
        return this.getThis();
    }
    
    public T mergeFramed(final InputStream input) throws IOException {
        final int length = readRawVarint32(input);
        final byte[] data = new byte[length];
        int r;
        for (int pos = 0; pos < length; pos += r) {
            r = input.read(data, pos, length - pos);
            if (r < 0) {
                throw new InvalidProtocolBufferException("Input stream ended before a full message frame could be read.");
            }
        }
        return this.mergeUnframed(data);
    }
    
    protected static <T> void addAll(final Iterable<T> values, final Collection<? super T> list) {
        if (values instanceof Collection) {
            final Collection<T> collection = (Collection<T>)(Collection)values;
            list.addAll((Collection<? extends T>)collection);
        }
        else {
            for (final T value : values) {
                list.add((Object)value);
            }
        }
    }
    
    protected static void writeGroup(final CodedOutputStream output, final int tag, final BaseMessage message) throws IOException {
        output.writeTag(tag, 3);
        message.writeUnframed(output);
        output.writeTag(tag, 4);
    }
    
    protected static <T extends BaseMessage> T readGroup(final CodedInputStream input, final int tag, final T group) throws IOException {
        group.mergeUnframed(input);
        input.checkLastTagWas(WireFormat.makeTag(tag, 4));
        return group;
    }
    
    protected static int computeGroupSize(final int tag, final BaseMessage message) {
        return CodedOutputStream.computeTagSize(tag) * 2 + message.serializedSizeUnframed();
    }
    
    protected static void writeMessage(final CodedOutputStream output, final int tag, final BaseMessage message) throws IOException {
        output.writeTag(tag, 2);
        message.writeFramed(output);
    }
    
    protected static int computeMessageSize(final int tag, final BaseMessage message) {
        return CodedOutputStream.computeTagSize(tag) + message.serializedSizeFramed();
    }
    
    protected List<String> prefix(final List<String> missingFields, final String prefix) {
        final ArrayList<String> rc = new ArrayList<String>(missingFields.size());
        for (final String v : missingFields) {
            rc.add(prefix + v);
        }
        return rc;
    }
    
    public static int readRawVarint32(final InputStream is) throws IOException {
        byte tmp = readRawByte(is);
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7F;
        if ((tmp = readRawByte(is)) >= 0) {
            result |= tmp << 7;
        }
        else {
            result |= (tmp & 0x7F) << 7;
            if ((tmp = readRawByte(is)) >= 0) {
                result |= tmp << 14;
            }
            else {
                result |= (tmp & 0x7F) << 14;
                if ((tmp = readRawByte(is)) >= 0) {
                    result |= tmp << 21;
                }
                else {
                    result |= (tmp & 0x7F) << 21;
                    result |= (tmp = readRawByte(is)) << 28;
                    if (tmp < 0) {
                        for (int i = 0; i < 5; ++i) {
                            if (readRawByte(is) >= 0) {
                                return result;
                            }
                        }
                        throw new InvalidProtocolBufferException("CodedInputStream encountered a malformed varint.");
                    }
                }
            }
        }
        return result;
    }
    
    protected static byte readRawByte(final InputStream is) throws IOException {
        final int rc = is.read();
        if (rc == -1) {
            throw new InvalidProtocolBufferException("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
        }
        return (byte)rc;
    }
}
