// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.JMSException;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.QueueSender;

public class ActiveMQQueueSender extends ActiveMQMessageProducer implements QueueSender
{
    protected ActiveMQQueueSender(final ActiveMQSession session, final ActiveMQDestination destination, final int sendTimeout) throws JMSException {
        super(session, session.getNextProducerId(), destination, sendTimeout);
    }
    
    @Override
    public Queue getQueue() throws JMSException {
        return (Queue)super.getDestination();
    }
    
    @Override
    public void send(final Queue queue, final Message message) throws JMSException {
        super.send(queue, message);
    }
    
    @Override
    public void send(final Queue queue, final Message message, final int deliveryMode, final int priority, final long timeToLive) throws JMSException {
        super.send(queue, message, deliveryMode, priority, timeToLive);
    }
}
