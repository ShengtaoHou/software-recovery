// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQSession;
import org.apache.camel.Endpoint;
import javax.jms.QueueSender;

public class CamelQueueSender extends CamelMessageProducer implements QueueSender
{
    public CamelQueueSender(final CamelQueue destination, final Endpoint endpoint, final ActiveMQSession session) throws JMSException {
        super(destination, endpoint, session);
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
