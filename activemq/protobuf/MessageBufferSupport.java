// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.util.Iterator;
import java.util.Collection;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class MessageBufferSupport
{
    public static final String FORZEN_ERROR_MESSAGE = "Modification not allowed after object has been fozen.  Try modifying a copy of this object.";
    
    public static Buffer toUnframedBuffer(final MessageBuffer message) {
        try {
            final int size = message.serializedSizeUnframed();
            final BufferOutputStream baos = new BufferOutputStream(size);
            final CodedOutputStream output = new CodedOutputStream(baos);
            message.writeUnframed(output);
            final Buffer rc = baos.toBuffer();
            assert rc.length == size : "Did not write as much data as expected.";
            return rc;
        }
        catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }
    
    public static Buffer toFramedBuffer(final MessageBuffer message) {
        try {
            final int size = message.serializedSizeFramed();
            final BufferOutputStream baos = new BufferOutputStream(size);
            final CodedOutputStream output = new CodedOutputStream(baos);
            message.writeFramed(output);
            final Buffer rc = baos.toBuffer();
            assert rc.length == size : "Did not write as much data as expected.";
            return rc;
        }
        catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }
    
    public static void writeMessage(final CodedOutputStream output, final int tag, final MessageBuffer message) throws IOException {
        output.writeTag(tag, 2);
        message.writeFramed(output);
    }
    
    public static int computeMessageSize(final int tag, final MessageBuffer message) {
        return CodedOutputStream.computeTagSize(tag) + message.serializedSizeFramed();
    }
    
    public static Buffer readFrame(final InputStream input) throws IOException {
        final int length = readRawVarint32(input);
        final byte[] data = new byte[length];
        int r;
        for (int pos = 0; pos < length; pos += r) {
            r = input.read(data, pos, length - pos);
            if (r < 0) {
                throw new InvalidProtocolBufferException("Input stream ended before a full message frame could be read.");
            }
        }
        return new Buffer(data);
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
    
    public static byte readRawByte(final InputStream is) throws IOException {
        final int rc = is.read();
        if (rc == -1) {
            throw new InvalidProtocolBufferException("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either than the input has been truncated or that an embedded message misreported its own length.");
        }
        return (byte)rc;
    }
    
    public static <T> void addAll(final Iterable<T> values, final Collection<? super T> list) {
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
}
