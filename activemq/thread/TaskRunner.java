// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.thread;

public interface TaskRunner
{
    void wakeup() throws InterruptedException;
    
    void shutdown() throws InterruptedException;
    
    void shutdown(final long p0) throws InterruptedException;
}
