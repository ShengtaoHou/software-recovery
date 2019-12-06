// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQMessage;

final class NullMessageReference implements QueueMessageReference
{
    private final ActiveMQMessage message;
    private volatile int references;
    
    NullMessageReference() {
        this.message = new ActiveMQMessage();
    }
    
    @Override
    public void drop() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public LockOwner getLockOwner() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public boolean isAcked() {
        return false;
    }
    
    @Override
    public boolean isDropped() {
        return false;
    }
    
    @Override
    public boolean lock(final LockOwner subscription) {
        return true;
    }
    
    @Override
    public void setAcked(final boolean b) {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public boolean unlock() {
        return true;
    }
    
    @Override
    public int decrementReferenceCount() {
        return --this.references;
    }
    
    @Override
    public long getExpiration() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public String getGroupID() {
        return null;
    }
    
    @Override
    public int getGroupSequence() {
        return 0;
    }
    
    @Override
    public Message getMessage() {
        return this.message;
    }
    
    @Override
    public Message getMessageHardRef() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public MessageId getMessageId() {
        return this.message.getMessageId();
    }
    
    @Override
    public int getRedeliveryCounter() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public int getReferenceCount() {
        return this.references;
    }
    
    @Override
    public Destination getRegionDestination() {
        return null;
    }
    
    @Override
    public int getSize() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public ConsumerId getTargetConsumerId() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public void incrementRedeliveryCounter() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public int incrementReferenceCount() {
        return ++this.references;
    }
    
    @Override
    public boolean isExpired() {
        return false;
    }
    
    @Override
    public boolean isPersistent() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public boolean isAdvisory() {
        return false;
    }
}
