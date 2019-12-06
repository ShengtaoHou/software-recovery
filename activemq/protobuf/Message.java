// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface Message<T>
{
    T clone() throws CloneNotSupportedException;
    
    int serializedSizeUnframed();
    
    int serializedSizeFramed();
    
    void clear();
    
    T assertInitialized() throws UninitializedMessageException;
    
    T mergeFrom(final T p0);
    
    T mergeUnframed(final byte[] p0) throws InvalidProtocolBufferException;
    
    T mergeFramed(final byte[] p0) throws InvalidProtocolBufferException;
    
    T mergeUnframed(final Buffer p0) throws InvalidProtocolBufferException;
    
    T mergeFramed(final Buffer p0) throws InvalidProtocolBufferException;
    
    T mergeUnframed(final InputStream p0) throws IOException;
    
    T mergeFramed(final InputStream p0) throws IOException;
    
    T mergeUnframed(final CodedInputStream p0) throws IOException;
    
    T mergeFramed(final CodedInputStream p0) throws IOException;
    
    Buffer toUnframedBuffer();
    
    Buffer toFramedBuffer();
    
    byte[] toUnframedByteArray();
    
    byte[] toFramedByteArray();
    
    void writeUnframed(final CodedOutputStream p0) throws IOException;
    
    void writeFramed(final CodedOutputStream p0) throws IOException;
    
    void writeUnframed(final OutputStream p0) throws IOException;
    
    void writeFramed(final OutputStream p0) throws IOException;
}
