// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import java.nio.ByteBuffer;
import org.apache.activemq.Service;

public interface ByteBufferPool extends Service
{
    ByteBuffer borrowBuffer();
    
    void returnBuffer(final ByteBuffer p0);
    
    void setDefaultSize(final int p0);
}
