// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.util.List;
import org.apache.activemq.command.MessageDispatch;

public interface MessageDispatchChannel
{
    void enqueue(final MessageDispatch p0);
    
    void enqueueFirst(final MessageDispatch p0);
    
    boolean isEmpty();
    
    MessageDispatch dequeue(final long p0) throws InterruptedException;
    
    MessageDispatch dequeueNoWait();
    
    MessageDispatch peek();
    
    void start();
    
    void stop();
    
    void close();
    
    void clear();
    
    boolean isClosed();
    
    int size();
    
    Object getMutex();
    
    boolean isRunning();
    
    List<MessageDispatch> removeAll();
}
