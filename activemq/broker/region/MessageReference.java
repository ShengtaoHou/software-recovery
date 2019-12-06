// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageId;

public interface MessageReference
{
    MessageId getMessageId();
    
    Message getMessageHardRef();
    
    Message getMessage();
    
    boolean isPersistent();
    
    Message.MessageDestination getRegionDestination();
    
    int getRedeliveryCounter();
    
    void incrementRedeliveryCounter();
    
    int getReferenceCount();
    
    int incrementReferenceCount();
    
    int decrementReferenceCount();
    
    ConsumerId getTargetConsumerId();
    
    int getSize();
    
    long getExpiration();
    
    String getGroupID();
    
    int getGroupSequence();
    
    boolean isExpired();
    
    boolean isDropped();
    
    boolean isAdvisory();
}
