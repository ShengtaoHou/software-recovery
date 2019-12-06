// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQDestination;

public class SharedDeadLetterStrategy extends AbstractDeadLetterStrategy
{
    public static final String DEFAULT_DEAD_LETTER_QUEUE_NAME = "ActiveMQ.DLQ";
    private ActiveMQDestination deadLetterQueue;
    
    public SharedDeadLetterStrategy() {
        this.deadLetterQueue = new ActiveMQQueue("ActiveMQ.DLQ");
    }
    
    @Override
    public ActiveMQDestination getDeadLetterQueueFor(final Message message, final Subscription subscription) {
        return this.deadLetterQueue;
    }
    
    public ActiveMQDestination getDeadLetterQueue() {
        return this.deadLetterQueue;
    }
    
    public void setDeadLetterQueue(final ActiveMQDestination deadLetterQueue) {
        this.deadLetterQueue = deadLetterQueue;
    }
    
    @Override
    public boolean isDLQ(final ActiveMQDestination destination) {
        return destination.equals(this.deadLetterQueue);
    }
}
