// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.Queue;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerId;
import javax.jms.QueueReceiver;

public class ActiveMQQueueReceiver extends ActiveMQMessageConsumer implements QueueReceiver
{
    protected ActiveMQQueueReceiver(final ActiveMQSession theSession, final ConsumerId consumerId, final ActiveMQDestination destination, final String selector, final int prefetch, final int maximumPendingMessageCount, final boolean asyncDispatch) throws JMSException {
        super(theSession, consumerId, destination, null, selector, prefetch, maximumPendingMessageCount, false, false, asyncDispatch, null);
    }
    
    @Override
    public Queue getQueue() throws JMSException {
        this.checkClosed();
        return (Queue)super.getDestination();
    }
}
