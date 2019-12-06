// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Destination;
import javax.jms.QueueSender;

public class PooledQueueSender extends PooledProducer implements QueueSender
{
    public PooledQueueSender(final QueueSender messageProducer, final Destination destination) throws JMSException {
        super(messageProducer, destination);
    }
    
    @Override
    public void send(final Queue queue, final Message message, final int i, final int i1, final long l) throws JMSException {
        this.getQueueSender().send(queue, message, i, i1, l);
    }
    
    @Override
    public void send(final Queue queue, final Message message) throws JMSException {
        this.getQueueSender().send(queue, message);
    }
    
    @Override
    public Queue getQueue() throws JMSException {
        return (Queue)this.getDestination();
    }
    
    protected QueueSender getQueueSender() {
        return (QueueSender)this.getMessageProducer();
    }
}
