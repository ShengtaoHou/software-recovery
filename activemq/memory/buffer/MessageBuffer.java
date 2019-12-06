// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory.buffer;

public interface MessageBuffer
{
    int getSize();
    
    MessageQueue createMessageQueue();
    
    void onSizeChanged(final MessageQueue p0, final int p1, final int p2);
    
    void clear();
}
