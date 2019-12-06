// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

public interface QueueMessageReference extends MessageReference
{
    public static final QueueMessageReference NULL_MESSAGE = new NullMessageReference();
    
    boolean isAcked();
    
    void setAcked(final boolean p0);
    
    void drop();
    
    boolean isDropped();
    
    boolean lock(final LockOwner p0);
    
    boolean unlock();
    
    LockOwner getLockOwner();
}
