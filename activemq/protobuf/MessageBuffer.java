// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.protobuf;

import java.io.OutputStream;
import java.io.IOException;

public interface MessageBuffer<B, MB extends MessageBuffer> extends PBMessage<B, MB>
{
    int serializedSizeUnframed();
    
    int serializedSizeFramed();
    
    Buffer toUnframedBuffer();
    
    Buffer toFramedBuffer();
    
    byte[] toUnframedByteArray();
    
    byte[] toFramedByteArray();
    
    void writeUnframed(final CodedOutputStream p0) throws IOException;
    
    void writeFramed(final CodedOutputStream p0) throws IOException;
    
    void writeUnframed(final OutputStream p0) throws IOException;
    
    void writeFramed(final OutputStream p0) throws IOException;
}
