// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

public interface TimeStampStream
{
    boolean isWriting();
    
    long getWriteTimestamp();
}
