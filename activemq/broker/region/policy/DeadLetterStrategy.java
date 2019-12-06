// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.Message;

public interface DeadLetterStrategy
{
    boolean isSendToDeadLetterQueue(final Message p0);
    
    ActiveMQDestination getDeadLetterQueueFor(final Message p0, final Subscription p1);
    
    boolean isProcessExpired();
    
    void setProcessExpired(final boolean p0);
    
    boolean isProcessNonPersistent();
    
    void setProcessNonPersistent(final boolean p0);
    
    boolean isDLQ(final ActiveMQDestination p0);
    
    void rollback(final Message p0);
}
